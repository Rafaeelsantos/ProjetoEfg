package com.redesocial.efg.service;

import java.util.Optional;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.redesocial.efg.model.LoginUsuario;
import com.redesocial.efg.model.Usuario;
import com.redesocial.efg.repository.UsuarioRepository;
import com.redesocial.efg.security.JwtService;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();

		usuario.setSenha(criptografarSenha(usuario.getSenha()));

		return Optional.of(usuarioRepository.save(usuario));
	}

	public Optional<Usuario> atualizarUsuario(Usuario usuario) {

		if (usuarioRepository.findById(usuario.getId()).isPresent()) {

			Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());

			if ((buscaUsuario.isPresent()) && (buscaUsuario.get().getId() != usuario.getId()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);

			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			return Optional.ofNullable(usuarioRepository.save(usuario));

		}

		return Optional.empty();

	}

	public Optional<LoginUsuario> autenticarUsuario(Optional<LoginUsuario> loginUsuario) {

		var credenciais = new UsernamePasswordAuthenticationToken(loginUsuario.get().getUsuario(),
				loginUsuario.get().getSenha());

		Authentication authentication = authenticationManager.authenticate(credenciais);
		if (org.springframework.security.core.Authentication.isAuthenticated()) {

			Optional<Usuario> usuario = usuarioRepository.findByUsuario(loginUsuario.get().getUsuario());

			if (usuario.isPresent()) {

				loginUsuario.get().setId(usuario.get().getId());
				loginUsuario.get().setNome(usuario.get().getNome());
				loginUsuario.get().setFoto(usuario.get().getFoto());
				loginUsuario.get().setSenha("");
				loginUsuario.get().setToken(gerarToken(loginUsuario.get().getUsuario()));

				return loginUsuario;
			}
		

		return Optional.empty();

	}

	private String gerarToken(String usuario) {
		return "Bearer " + jwtService.generateToken(usuario);
	}

	private String criptografarSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(senha);
	}
}