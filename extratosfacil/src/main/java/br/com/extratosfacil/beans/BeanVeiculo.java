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
public class BeanVeiculo implements Serializable {

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

	private List<Integer> cat = new ArrayList<Integer>();

	private List<String> categorias = new ArrayList<String>();

	private CommandButton botaoNovo;

	private String placaErrada = "";

	private String categoriaSelecionada = "";

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

	public String getPlacaErrada() {
		return placaErrada;
	}

	public void setPlacaErrada(String placaErrada) {
		this.placaErrada = placaErrada;
	}

	public List<Integer> getCat() {
		return cat;
	}

	public List<String> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<String> categorias) {
		this.categorias = categorias;
	}

	public void setCat(List<Integer> cat) {
		this.cat = cat;
	}

	public String getCategoriaSelecionada() {
		return categoriaSelecionada;
	}

	public void setCategoriaSelecionada(String categoriaSelecionada) {
		this.categoriaSelecionada = categoriaSelecionada;
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

		int x = categorias.indexOf(categoriaSelecionada);

		try {
			this.veiculo.setCategoria(this.cat.get(x));
		} catch (Exception e) {
			// TODO: handle exception
		}

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
		this.cat.add(1);
		this.cat.add(2);
		this.cat.add(3);
		this.cat.add(4);
		this.cat.add(5);
		this.cat.add(6);
		this.cat.add(61);
		this.cat.add(62);
		this.cat.add(63);
		this.cat.add(64);
		this.cat.add(9);

		this.categorias.add("Carro");
		this.categorias.add("Caminhão 2 Eixos");
		this.categorias.add("Caminhão 3 Eixos");
		this.categorias.add("Caminhão 4 Eixos");
		this.categorias.add("Caminhão 5 Eixos");
		this.categorias.add("Caminhão 6 Eixos");
		this.categorias.add("Caminhão 7 Eixos");
		this.categorias.add("Caminhão 8 Eixos");
		this.categorias.add("Caminhão 9 Eixos");
		this.categorias.add("Caminhão 10 Eixos");
		this.categorias.add("Moto");
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

	public void sendEmailPlaca() {
		if (placaErrada == null || placaErrada.equals("")) {
			Mensagem.send("Informe a Placa", Mensagem.ERROR);
		} else {

			Veiculo temp = new Veiculo();
			temp.setPlacaVeiculo(placaErrada.toUpperCase());
			temp = this.session.find(temp);

			if (temp == null) {
				Mensagem.send("Placa Informada Não Encontrada!", Mensagem.ERROR);
			} else {
				this.session.sendEmailPlaca(placaErrada);
			}
		}
	}

}