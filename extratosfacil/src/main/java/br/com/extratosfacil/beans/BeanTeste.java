package br.com.extratosfacil.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.event.FileUploadEvent;

import br.com.extratosfacil.constantes.Mensagem;
import br.com.extratosfacil.entities.Veiculo;
import br.com.extratosfacil.entities.planilha.ItemPlanilhaDownload;
import br.com.extratosfacil.session.SessionTeste;

/**
 * Bean que representa a entidade Empresa
 * 
 * @author Paulo Henrique da Silva
 * @since 29/07/2015
 * @version 1.0
 * @category Bean
 */

@ManagedBean
@SessionScoped
public class BeanTeste implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*-------------------------------------------------------------------
	 * 		 					ATTRIBUTES
	 *-------------------------------------------------------------------*/

	private Veiculo veiculo = new Veiculo();

	private SessionTeste session = new SessionTeste();

	private List<ItemPlanilhaDownload> itens = new ArrayList<ItemPlanilhaDownload>();

	private int erros = 0;

	private List<Integer> cat = new ArrayList<Integer>();

	private List<String> categorias = new ArrayList<String>();

	private String categoriaSelecionada = "";

	/*-------------------------------------------------------------------
	 * 		 					CONSTRUCTOR
	 *-------------------------------------------------------------------*/

	public BeanTeste() {
		this.carragaCategoria();
	}

	/*-------------------------------------------------------------------
	 * 		 					BEHAVIORS
	 *-------------------------------------------------------------------*/

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public String getCategoriaSelecionada() {
		return categoriaSelecionada;
	}

	public void setCategoriaSelecionada(String categoriaSelecionada) {
		this.categoriaSelecionada = categoriaSelecionada;
	}

	public List<Integer> getCat() {
		return cat;
	}

	public void setCat(List<Integer> cat) {
		this.cat = cat;
	}

	public List<String> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<String> categorias) {
		this.categorias = categorias;
	}

	public int getErros() {
		return erros;
	}

	public void setErros(int erros) {
		this.erros = erros;
	}

	public SessionTeste getSession() {
		return session;
	}

	public void setSession(SessionTeste session) {
		this.session = session;
	}

	public List<ItemPlanilhaDownload> getItens() {
		return itens;
	}

	public void setItens(List<ItemPlanilhaDownload> itens) {
		this.itens = itens;
	}

	/*-------------------------------------------------------------------
	 * 		 					METHODS
	 *-------------------------------------------------------------------*/

	public void fileUploadAction(FileUploadEvent event) {

		int x = categorias.indexOf(categoriaSelecionada);

		try {
			this.veiculo.setCategoria(this.cat.get(x));
		} catch (Exception e) {
			// TODO: handle exception
		}

		if ((this.veiculo == null) || (this.veiculo.getPlacaVeiculo() == null)
				|| (this.veiculo.getCategoria() == null)) {
			Mensagem.send("Preencha os dados do Ve�culo e clique em gravar",
					Mensagem.ERROR);
			return;
		}

		try {

			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();

			String realPath = context.getRealPath("/");

			// Aqui cria o diretorio caso n�o exista
			File file = new File(realPath + "try" + File.separator);
			file.mkdirs();
			// Mensagem.send("Criou Diretorio", Mensagem.ERROR);
			byte[] arquivo = event.getFile().getContents();
			boolean xlsx = event.getFile().getFileName().indexOf("xlsx") >= 0;
			String caminho = realPath + "try" + File.separator
					+ event.getFile().getFileName();

			System.out.println(caminho);

			// esse trecho grava o arquivo no diret�rio
			FileOutputStream fos = new FileOutputStream(caminho);
			fos.write(arquivo);
			fos.close();
			this.erros = 0;

			Object workbook = this.session.validaPlanilha(caminho, xlsx);

			if (workbook == null) {
				// workbook = this.session.validaXml(caminho);
				// if (workbook != null) {
				itens = this.session.carregaXml(caminho, veiculo);
				this.erros = session.getErros();
				// }
			} else {
				itens = this.session.carregaPlanilha(workbook, veiculo);
				this.erros = session.getErros();
			}

			// Mensagem.send("Finalizou", Mensagem.ERROR);
			File f = new File(caminho);
			f.delete();

			if (itens.isEmpty()) {
				Mensagem.send("N�o foram encontrados erros para este veiculo",
						Mensagem.INFO);
			}

			System.gc();

		} catch (Exception ex) {
			System.gc();
			ex.printStackTrace();
			Mensagem.send(Mensagem.MSG_PLANILHA_ERRADA, Mensagem.ERROR);
			Mensagem.send(ex.getMessage(), Mensagem.ERROR);
		}
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
		this.categorias.add("Caminh�o 2 Eixos");
		this.categorias.add("Caminh�o 3 Eixos");
		this.categorias.add("Caminh�o 4 Eixos");
		this.categorias.add("Caminh�o 5 Eixos");
		this.categorias.add("Caminh�o 6 Eixos");
		this.categorias.add("Caminh�o 7 Eixos");
		this.categorias.add("Caminh�o 8 Eixos");
		this.categorias.add("Caminh�o 9 Eixos");
		this.categorias.add("Caminh�o 10 Eixos");
		this.categorias.add("Moto");
	}

}