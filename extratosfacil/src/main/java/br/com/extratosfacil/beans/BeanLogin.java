package br.com.extratosfacil.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.com.extratosfacil.constantes.Mensagem;
import br.com.extratosfacil.constantes.Sessao;
import br.com.extratosfacil.entities.Empresa;
import br.com.extratosfacil.session.SessionUsuario;

@ManagedBean
@SessionScoped
public class BeanLogin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean usuarioLogado = false;

	private SessionUsuario session = new SessionUsuario();

	private Empresa usuario = new Empresa();

	private String confSenha = new String();

	private String login;

	private String senha;

	public Boolean getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(Boolean usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public Empresa getUsuario() {
		return usuario;
	}

	public void setUsuario(Empresa usuario) {
		this.usuario = usuario;
	}

	public String getConfSenha() {
		return confSenha;
	}

	public void setConfSenha(String confSenha) {
		this.confSenha = confSenha;
	}

	public String getLogin() {
		return login;
	}

	public SessionUsuario getSession() {
		return session;
	}

	public void setSession(SessionUsuario session) {
		this.session = session;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public BeanLogin() {
	}

	public String fazerLogin() {

		if ((usuario.getSenha() == null) || (usuario.getLogin() == null)) {
			Mensagem.send(Mensagem.MSG_USER_NAO_ENCONTRADO, Mensagem.ERROR);
			return "";
		}
		if ((usuario.getLogin().trim().equals(""))
				&& (usuario.getSenha().trim().equals(""))) {
			Mensagem.send(Mensagem.MSG_USER_NAO_ENCONTRADO, Mensagem.ERROR);
			return "";
		}

		String login = usuario.getLogin();

		this.usuario = session.efetuarLogin(this.usuario);

		if (this.usuario == null) {
			usuario = new Empresa();
			usuario.setLogin(login);
			Mensagem.send(Mensagem.MSG_USER_NAO_ENCONTRADO, Mensagem.ERROR);
			return "";
		}

		if (this.usuario.getStatus().equals("New")) {
			Mensagem.send(Mensagem.MSG_USER_NAO_CONFIRMADO, Mensagem.ERROR);
			return "";
		}

		usuarioLogado = Boolean.TRUE;
		Sessao.setEmpresaSessao(usuario);

		session.clean();
		Sessao.redireciona("index.html");
		return "";
	}

	public String logout() {
		this.usuario.clean();
		Sessao.setEmpresaSessao(null);

		FacesContext.getCurrentInstance().getExternalContext()
				.invalidateSession();

		return "login.html";
	}

	public void isLogado() {
		if (!usuarioLogado) {
			Sessao.redireciona("login.html");
		}
	}

	public void isLogin() {
		if (usuarioLogado) {
			Sessao.redireciona("index.html");
		}
	}

	public void redirecionaLogin() {
		Sessao.redireciona("login.html");
	}
}
