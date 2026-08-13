package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.ContatoEmergenciaRequest;
import com.example.tcc_backend.controller.response.ContatoEmergenciaResponse;
import com.example.tcc_backend.domain.ContatoEmergencia;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.mapper.ContatoEmergenciaMapper;
import com.example.tcc_backend.repository.ContatoEmergenciaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ContatoEmergenciaService {

    private static final long LIMITE_CONTATOS = 10;

    private final ContatoEmergenciaRepository contatoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ContatoEmergenciaService(ContatoEmergenciaRepository contatoRepository,
                                    UsuarioAutenticadoService usuarioAutenticadoService) {
        this.contatoRepository = contatoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional(readOnly = true)
    public List<ContatoEmergenciaResponse> listar() {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        return contatoRepository.findAllByUsuarioIdOrderByPrincipalDescNomeAsc(usuario.getId())
                .stream()
                .map(ContatoEmergenciaMapper::toResponse)
                .toList();
    }

    @Transactional
    public ContatoEmergenciaResponse criar(ContatoEmergenciaRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        if (contatoRepository.countByUsuarioId(usuario.getId()) >= LIMITE_CONTATOS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O limite de 10 contatos foi atingido");
        }

        validarTelefone(request.getTelefone());
        ContatoEmergencia contato = ContatoEmergenciaMapper.toEntity(request);
        contato.setUsuario(usuario);
        desmarcarPrincipalAtualSeNecessario(usuario.getId(), contato.getPrincipal());
        return ContatoEmergenciaMapper.toResponse(contatoRepository.save(contato));
    }

    @Transactional
    public ContatoEmergenciaResponse atualizar(Long id, ContatoEmergenciaRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        ContatoEmergencia contato = buscarDoUsuario(id, usuario.getId());
        validarTelefone(request.getTelefone());
        desmarcarPrincipalAtualSeNecessario(usuario.getId(), request.isPrincipal());
        ContatoEmergenciaMapper.atualizarEntidade(contato, request);
        return ContatoEmergenciaMapper.toResponse(contatoRepository.save(contato));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        contatoRepository.delete(buscarDoUsuario(id, usuario.getId()));
    }

    private ContatoEmergencia buscarDoUsuario(Long id, Long usuarioId) {
        return contatoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contato não encontrado"));
    }

    private void desmarcarPrincipalAtualSeNecessario(Long usuarioId, boolean novoPrincipal) {
        if (!novoPrincipal) {
            return;
        }
        List<ContatoEmergencia> principais = contatoRepository.findAllByUsuarioIdAndPrincipalTrue(usuarioId);
        principais.forEach(contato -> contato.setPrincipal(false));
        contatoRepository.saveAll(principais);
    }

    private void validarTelefone(String telefone) {
        String digitos = telefone == null ? "" : telefone.replaceAll("\\D", "");
        if (digitos.length() < 8 || digitos.length() > 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone inválido");
        }
    }
}
