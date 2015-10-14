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

	private List<Integer> categorias = new ArrayList<Integer>();

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

	public List<Integer> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Integer> categorias) {
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

		if ((this.veiculo == null) || (this.veiculo.getPlacaVeiculo() == null)
				|| (this.veiculo.getCategoria() == null)) {
			Mensagem.send("Preencha os dados do Veículo e clique em gravar",
					Mensagem.ERROR);
			return;
		}

		try {

			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();

			String realPath = context.getRealPath("/");

			// Aqui cria o diretorio caso nï¿½o exista
			File file = new File(realPath + "try" + File.separator);
			file.mkdirs();
			// Mensagem.send("Criou Diretorio", Mensagem.ERROR);
			byte[] arquivo = event.getFile().getContents();
			boolean xlsx = event.getFile().getFileName().indexOf("xlsx") >= 0;
			String caminho = realPath + "try" + File.separator
					+ event.getFile().getFileName();

			System.out.println(caminho);

			// esse trecho grava o arquivo no diretï¿½rio
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
				Mensagem.send("Não foram encontrados erros para este veiculo",
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

}