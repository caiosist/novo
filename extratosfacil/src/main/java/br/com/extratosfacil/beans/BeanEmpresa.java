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
	private boolean confirmar = false;
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

	private List<String> estadosLabel = new ArrayList<String>();

	private List<String> cidadesLabel = new ArrayList<String>();

	private List<Cidade> cidades = new ArrayList<Cidade>();

	private List<Estado> estados = new ArrayList<Estado>();

	private String selecionado = new String();

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

	public boolean isConfirmar() {
		this.confirmaCadastro();
		return confirmar;
	}

	public void setConfirmar(boolean confirmar) {
		this.confirmar = confirmar;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(String selecionado) {
		this.selecionado = selecionado;
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

	public List<String> getEstadosLabel() {
		return estadosLabel;
	}

	public void setEstadosLabel(List<String> estadosLabel) {
		this.estadosLabel = estadosLabel;
	}

	public List<String> getCidadesLabel() {
		return cidadesLabel;
	}

	public void setCidadesLabel(List<String> cidadesLabel) {
		this.cidadesLabel = cidadesLabel;
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
			this.session.sendEmailConfirmacao(this.empresa);
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
		for (int i = 0; i < this.estados.size(); i++) {
			this.estadosLabel.add(this.estados.get(i).getNomeEstado());
		}
		this.session.clean();
	}

	public void carregaCidades() {
		for (int i = 0; i < this.estados.size(); i++) {
			if (estados.get(i).getNomeEstado().equals(this.selecionado)) {
				this.empresa.getCidade().setEstado(estados.get(i));
			}
		}

		this.cidades.removeAll(cidades);
		this.cidadesLabel.removeAll(cidadesLabel);
		this.cidades.addAll(this.session.findCidades(this.empresa.getCidade()
				.getEstado()));
		for (int i = 0; i < this.cidades.size(); i++) {
			this.cidadesLabel.add(this.cidades.get(i).getNomeCidade());
		}
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

	public void setCidadeNome() {
		for (int i = 0; i < cidades.size(); i++) {
			if (cidades.get(i).getNomeCidade()
					.equals(this.empresa.getCidade().getNomeCidade())) {
				this.empresa.setCidade(this.cidades.get(i));
			}
		}
	}
}
