package br.com.extratosfacil.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.extratosfacil.constantes.Mensagem;
import br.com.extratosfacil.constantes.Sessao;
import br.com.extratosfacil.entities.Empresa;
import br.com.extratosfacil.entities.location.Cidade;
import br.com.extratosfacil.entities.location.Estado;
import br.com.extratosfacil.session.SessionEmpresa;

@ManagedBean
@ViewScoped
public class BeanEmpresa implements Serializable {

	/*-------------------------------------------------------------------
	 * 		 					ATTRIBUTES
	 *-------------------------------------------------------------------*/

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private Empresa empresa = new Empresa();
	/**
	 * 
	 */
	private boolean pessoaFisica = false;
	/**
	 * 
	 */
	private boolean aceito = false;
	/**
	 * 
	 */
	private SessionEmpresa session = new SessionEmpresa();
	/**
	 * 
	 */
	private String confSenha;
	/**
	 * 
	 */
	private String senha;

	private List<Cidade> cidades = new ArrayList<Cidade>();

	private List<Estado> estados = new ArrayList<Estado>();

	/*-------------------------------------------------------------------
	 * 		 					CONSTRUCTOR
	 *-------------------------------------------------------------------*/

	public BeanEmpresa() {
		this.carregaEstados();
	}

	/*-------------------------------------------------------------------
	 * 		 					BEHAVIORS
	 *-------------------------------------------------------------------*/

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public boolean isPessoaFisica() {
		return pessoaFisica;
	}

	public boolean isAceito() {
		return aceito;
	}

	public void setAceito(boolean aceito) {
		this.aceito = aceito;
	}

	public String getConfSenha() {
		return confSenha;
	}

	public void setConfSenha(String confSenha) {
		this.confSenha = confSenha;
	}

	public List<Cidade> getCidades() {
		return cidades;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

	public List<Estado> getEstados() {
		return estados;
	}

	public void setEstados(List<Estado> estados) {
		this.estados = estados;
	}

	public void setPessoaFisica(boolean pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}

	public SessionEmpresa getSession() {
		return session;
	}

	public void setSession(SessionEmpresa session) {
		this.session = session;
	}

	/*-------------------------------------------------------------------
	 * 		 					METHODS
	 *-------------------------------------------------------------------*/

	public String save() {
		if (!aceito) {
			Mensagem.send(Mensagem.MSG_ACEITO, Mensagem.ERROR);
			return "";
		}
		if (!empresa.getSenha().equals(this.confSenha)) {
			Mensagem.send(Mensagem.MSG_CONF_SENHA, Mensagem.ERROR);
			return "";
		}
		if (this.session.save(this.empresa, this.pessoaFisica)) {
			Sessao.redireciona("subscribe.html");
			return "";
		}
		this.session.clean();
		return "";
	}

	public void update() {
		this.session.update(this.empresa, this.pessoaFisica);
	}

	public void carregaEstados() {
		this.estados = this.session.findEstados(new Estado());
		this.session.clean();
	}

	public void carregaCidades() {
		this.cidades = this.session.findCidades(this.empresa.getCidade()
				.getEstado());
		this.session.clean();
	}

	public void reenviarEmail() {
		if (this.empresa.getEmail() == null
				|| this.empresa.getEmail().equals("")) {
			Mensagem.send(Mensagem.MSG_EMAIL_INVALIDO, Mensagem.ERROR);
			return;
		}
		this.empresa = this.session.find(this.empresa);
		if (empresa == null) {
			Mensagem.send(Mensagem.MSG_EMAIL_INVALIDO, Mensagem.ERROR);
			this.empresa = new Empresa();
		} else {
			this.session.sendEmailConfirmacao(this.empresa);
			Mensagem.send(Mensagem.MSG_EMAIL_ENVIADO, Mensagem.INFO);
		}
		// this.reinit();
	}

	public String redirecionaLogin() {
		return "login.html";
	}

	public boolean confirmaCadastro() {
		this.empresa = this.session.validaConfirmar();
		if (this.empresa == null) {
			this.empresa = new Empresa();
			Sessao.redireciona("login.html");
			return false;
		}
		this.empresa.setStatus("Pendente");
		this.update();
		return true;
	}

	public void enviarEmailRecuperarSenha() {
		if (this.empresa.getEmail() == null
				|| this.empresa.getEmail().equals("")) {
			Mensagem.send(Mensagem.MSG_EMAIL_INVALIDO, Mensagem.ERROR);
			return;
		}
		this.empresa = this.session.enviarEmailRecuperarSenha(this.empresa);
		if (empresa.getId() != null) {
			Mensagem.send(Mensagem.EMAIL_ENVIADO, Mensagem.INFO);
			this.empresa.clean();
		}
	}

	public void alterarSenha() {
		if (this.senha.equals(this.confSenha)) {
			this.empresa.setSenha(this.senha);
			this.update();
		} else {
			Mensagem.send(Mensagem.MSG_CONF_SENHA, Mensagem.ERROR);
		}
	}

	public boolean validaRecovery() {
		this.empresa = this.session.validaRecovery();
		if (this.empresa == null) {
			this.empresa = new Empresa();
			Sessao.redireciona("login.html");
			return false;
		}
		return true;
	}

}
