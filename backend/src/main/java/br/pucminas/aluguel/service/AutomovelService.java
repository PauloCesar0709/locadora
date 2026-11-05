package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.AutomovelRequest;
import br.pucminas.aluguel.dto.AutomovelResponse;
import br.pucminas.aluguel.exception.AcessoNegadoException;
import br.pucminas.aluguel.exception.NegocioException;
import br.pucminas.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguel.model.Automovel;
import br.pucminas.aluguel.model.Locadora;
import br.pucminas.aluguel.model.Usuario;
import br.pucminas.aluguel.repository.AutomovelRepository;
import br.pucminas.aluguel.repository.BancoRepository;
import br.pucminas.aluguel.repository.ClienteRepository;
import br.pucminas.aluguel.repository.LocadoraRepository;
import br.pucminas.aluguel.repository.OfertaLocacaoRepository;
import br.pucminas.aluguel.security.UsuarioLogado;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Veículos gerenciados por uma locadora. Cada locadora vê e altera apenas os seus. */
@Service
public class AutomovelService {

    private final AutomovelRepository automovelRepository;
    private final LocadoraRepository locadoraRepository;
    private final BancoRepository bancoRepository;
    private final ClienteRepository clienteRepository;
    private final OfertaLocacaoRepository ofertaRepository;

    public AutomovelService(AutomovelRepository automovelRepository,
                            LocadoraRepository locadoraRepository,
                            BancoRepository bancoRepository,
                            ClienteRepository clienteRepository,
                            OfertaLocacaoRepository ofertaRepository) {
        this.automovelRepository = automovelRepository;
        this.locadoraRepository = locadoraRepository;
        this.bancoRepository = bancoRepository;
        this.clienteRepository = clienteRepository;
        this.ofertaRepository = ofertaRepository;
    }

    @Transactional(readOnly = true)
    public List<AutomovelResponse> listar(UsuarioLogado logado) {
        return automovelRepository.findByLocadoraIdOrderByIdAsc(logado.id()).stream()
                .map(Mapeador::automovel).toList();
    }

    @Transactional
    public AutomovelResponse criar(AutomovelRequest request, UsuarioLogado logado) {
        Locadora locadora = locadoraRepository.findById(logado.id())
                .orElseThrow(() -> new AcessoNegadoException("Apenas locadoras cadastram veículos."));
        String placa = request.placa().toUpperCase();
        String matricula = request.matricula().trim();
        if (automovelRepository.existsByPlaca(placa)) {
            throw new NegocioException("Já existe um veículo com esta placa.");
        }
        if (automovelRepository.existsByMatricula(matricula)) {
            throw new NegocioException("Já existe um veículo com esta matrícula.");
        }
        Automovel automovel = new Automovel();
        automovel.setLocadora(locadora);
        preencher(automovel, request, placa, matricula, locadora);
        return Mapeador.automovel(automovelRepository.save(automovel));
    }

    @Transactional
    public AutomovelResponse atualizar(Long id, AutomovelRequest request, UsuarioLogado logado) {
        Automovel automovel = carregarDaLocadora(id, logado);
        String placa = request.placa().toUpperCase();
        String matricula = request.matricula().trim();
        if (automovelRepository.existsByPlacaAndIdNot(placa, id)) {
            throw new NegocioException("Já existe outro veículo com esta placa.");
        }
        if (automovelRepository.existsByMatriculaAndIdNot(matricula, id)) {
            throw new NegocioException("Já existe outro veículo com esta matrícula.");
        }
        preencher(automovel, request, placa, matricula, automovel.getLocadora());
        return Mapeador.automovel(automovelRepository.save(automovel));
    }

    @Transactional
    public void excluir(Long id, UsuarioLogado logado) {
        Automovel automovel = carregarDaLocadora(id, logado);
        if (ofertaRepository.existsByAutomovelId(id)) {
            throw new NegocioException("Não é possível excluir: o veículo já foi usado em ofertas de locação. Desative a oferta.");
        }
        automovelRepository.delete(automovel);
    }

    private Automovel carregarDaLocadora(Long id, UsuarioLogado logado) {
        Automovel automovel = automovelRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));
        if (!automovel.getLocadora().getId().equals(logado.id())) {
            throw new AcessoNegadoException("Este veículo pertence a outra locadora.");
        }
        return automovel;
    }

    private void preencher(Automovel automovel, AutomovelRequest request, String placa, String matricula,
                           Locadora locadora) {
        automovel.setMatricula(matricula);
        automovel.setAno(request.ano());
        automovel.setMarca(request.marca().trim());
        automovel.setModelo(request.modelo().trim());
        automovel.setPlaca(placa);
        automovel.setProprietario(resolverProprietario(request, locadora));
    }

    /** O proprietário pode ser a própria locadora, um banco ou um cliente (pelo CPF). */
    private Usuario resolverProprietario(AutomovelRequest request, Locadora locadora) {
        return switch (request.tipoProprietario()) {
            case LOCADORA -> locadora;
            case BANCO -> {
                if (request.bancoId() == null) {
                    throw new NegocioException("Escolha o banco proprietário.");
                }
                yield bancoRepository.findById(request.bancoId())
                        .orElseThrow(() -> new NegocioException("Banco proprietário não encontrado."));
            }
            case CLIENTE -> {
                if (request.clienteCpf() == null) {
                    throw new NegocioException("Informe o CPF do cliente proprietário.");
                }
                yield clienteRepository.findByCpf(request.clienteCpf())
                        .orElseThrow(() -> new NegocioException("Nenhum cliente cadastrado com este CPF."));
            }
        };
    }
}
