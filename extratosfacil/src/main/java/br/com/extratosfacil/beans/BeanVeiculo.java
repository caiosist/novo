package br.com.extratosfacil.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.context.RequestContext;

import br.com.extratosfacil.constantes.Mensagem;
import br.com.extratosfacil.constantes.Sessao;
import br.com.extratosfacil.entities.Empresa;
import br.com.extratosfacil.entities.Veiculo;
import br.com.extratosfacil.session.SessionVeiculo;

/**
 * Bean que representa a entidade Veiculo
 * 
 * @author Paulo Henrique da Silva
 * @since 29/07/2015
 * @version 1.0
 * @category Bean
 */

@ManagedBean
@ViewScoped
public class BeanVeiculo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*-------------------------------------------------------------------
	 * 		 					ATTRIBUTES
	 *-------------------------------------------------------------------*/

	private Veiculo veiculo = new Veiculo();

	private Veiculo selected = new Veiculo();

	private Veiculo filtro = new Veiculo();

	private List<Veiculo> listaVeiculos = new ArrayList<Veiculo>();

	private SessionVeiculo session = new SessionVeiculo();

	private List<Integer> categorias = new ArrayList<Integer>();

	private CommandButton botaoNovo;

	/*-------------------------------------------------------------------
	 * 		 					CONSTRUCTOR
	 *-------------------------------------------------------------------*/

	public BeanVeiculo() {
		this.carragaCategoria();
		this.carregaVeiculo();
		this.verificaPlano();
	}

	/*-------------------------------------------------------------------
	 * 		 					BEHAVIORS
	 *-------------------------------------------------------------------*/

	public SessionVeiculo getSession() {
		return session;
	}

	public void setSession(SessionVeiculo session) {
		this.session = session;
	}

	public Veiculo getFiltro() {
		return filtro;
	}

	public boolean isStatusBotaoNovo() {
		return this.verificaPlano();
	}

	public List<Integer> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Integer> categorias) {
		this.categorias = categorias;
	}

	public CommandButton getBotaoNovo() {
		return botaoNovo;
	}

	public void setBotaoNovo(CommandButton botaoNovo) {
		this.botaoNovo = botaoNovo;
	}

	public void setFiltro(Veiculo filtro) {
		this.filtro = filtro;
	}

	public Veiculo getSelected() {
		return selected;
	}

	public void setSelected(Veiculo selected) {
		this.selected = selected;
	}

	public List<Veiculo> getListaVeiculos() {
		return listaVeiculos;
	}

	public void setListaVeiculos(List<Veiculo> listaVeiculos) {
		this.listaVeiculos = listaVeiculos;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	/*-------------------------------------------------------------------
	 * 		 					METHODS
	 *-------------------------------------------------------------------*/

	public void reinit() {
		this.veiculo.clean();
		this.filtro.clean();
		this.selected.clean();
		this.listaVeiculos.removeAll(listaVeiculos);
		this.carregaVeiculo();
	}

	public void limpar() {
		this.filtro.clean();
	}

	public String save() throws Exception {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean sucesso = false;

		if (this.veiculo.getId() != null) {
			return this.update();
		}

		if (this.session.save(veiculo)) {
			this.reinit();
			this.carregaVeiculo();
			Mensagem.send(Mensagem.MSG_SALVA, Mensagem.INFO);
			sucesso = true;
			context.addCallbackParam("sucesso", sucesso);
			this.verificaPlano();
			return "";
		}

		context.addCallbackParam("sucesso", sucesso);
		return "getVeiculo";
	}

	public String update() {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean sucesso = false;

		if (this.session.update(this.veiculo)) {
			this.reinit();
			this.carregaVeiculo();
			sucesso = true;
			context.addCallbackParam("sucesso", sucesso);
			this.verificaPlano();
			Mensagem.send(Mensagem.MSG_UPDATE, Mensagem.INFO);
			return "";
		}
			
		context.addCallbackParam("sucesso", sucesso);
		return null;

	}

	public void carregaVeiculo() {
		this.veiculo.setEmpresa(Sessao.getEmpresaSessao());
		this.listaVeiculos = this.session.findList(this.veiculo);
	}

	public void find() throws Exception {
		this.filtro.setEmpresa(Sessao.getEmpresaSessao());
		this.listaVeiculos = this.session.findList(this.filtro);
	}

	public void novo() {
		this.setVeiculo(new Veiculo());
	}

	private void carragaCategoria() {
		this.categorias.add(1);
		this.categorias.add(2);
		this.categorias.add(3);
		this.categorias.add(4);
		this.categorias.add(5);
		this.categorias.add(6);
		this.categorias.add(61);
		this.categorias.add(62);
		this.categorias.add(63);
		this.categorias.add(64);
		this.categorias.add(9);
	}

	private boolean verificaPlano() {
		Empresa empresaTemp = Sessao.getEmpresaSessao();

		if ((empresaTemp != null) && (botaoNovo != null)) {

			if (empresaTemp.getPlano().getQuantidadeVeiculos() <= this.listaVeiculos
					.size()) {
				this.botaoNovo.setDisabled(true);
			} else {
				this.botaoNovo.setDisabled(false);
			}
			return this.botaoNovo.isDisabled();
		}
		return false;

	}

}