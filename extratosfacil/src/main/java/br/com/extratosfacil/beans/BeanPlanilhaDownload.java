package br.com.extratosfacil.beans;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.extratosfacil.constantes.Sessao;
import br.com.extratosfacil.entities.planilha.PlanilhaDownload;
import br.com.extratosfacil.session.SessionPlanilhaDownload;

/**
 * Bean que representa a entidade Planilha Download
 * 
 * @author Paulo Henrique da Silva
 * @since 05/07/2015
 * @version 1.0
 * @category Bean
 */

@ManagedBean
@ViewScoped
public class BeanPlanilhaDownload implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*-------------------------------------------------------------------
	 * 		 					ATTRIBUTES
	 *-------------------------------------------------------------------*/

	private PlanilhaDownload planilhaDownload = new PlanilhaDownload();

	private PlanilhaDownload selected = new PlanilhaDownload();

	private SessionPlanilhaDownload session = new SessionPlanilhaDownload();

	private List<PlanilhaDownload> planilhas = new ArrayList<PlanilhaDownload>();

	private List<PlanilhaDownload> lastPlanilhas = new ArrayList<PlanilhaDownload>();

	private Date inicio = new Date();

	private Date fim = new Date();

	private Double total = 0.0;

	private StreamedContent file;

	/*-------------------------------------------------------------------
	 * 		 					CONSTRUCTOR
	 *-------------------------------------------------------------------*/

	public BeanPlanilhaDownload() {
		this.find();
	}

	/*-------------------------------------------------------------------
	 * 		 					GETTERS AND SETTERS
	 *-------------------------------------------------------------------*/

	public PlanilhaDownload getPlanilhaDownload() {
		return planilhaDownload;
	}

	public SessionPlanilhaDownload getSession() {
		return session;
	}

	public void setLastPlanilhas(List<PlanilhaDownload> lastPlanilhas) {
		this.lastPlanilhas = lastPlanilhas;
	}

	public Date getInicio() {
		return inicio;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public PlanilhaDownload getSelected() {
		return selected;
	}

	public void setSelected(PlanilhaDownload selected) {
		this.selected = selected;
	}

	public StreamedContent getFile() {
		String path = this.selected.getPath();
		String contentType = FacesContext.getCurrentInstance()
				.getExternalContext().getMimeType(path);
		try {
			file = new DefaultStreamedContent(new FileInputStream(path),
					contentType, "Corre��o" + this.getData(selected.getData())
							+ ".xls");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public List<PlanilhaDownload> getLastPlanilhas() {
		this.carregaLastPlanilha();
		return lastPlanilhas;
	}

	public void setSession(SessionPlanilhaDownload session) {
		this.session = session;
	}

	public void setPlanilhaDownload(PlanilhaDownload planilhaDownload) {
		this.planilhaDownload = planilhaDownload;
	}

	public List<PlanilhaDownload> getPlanilhas() {
		return planilhas;
	}

	public void setPlanilhas(List<PlanilhaDownload> planilhas) {
		this.planilhas = planilhas;
	}

	/*-------------------------------------------------------------------
	 * 		 					METHODS
	 *-------------------------------------------------------------------*/
	public void find() {
		this.planilhaDownload.clean();
		;
		this.planilhaDownload.setEmpresa(Sessao.getEmpresaSessao());
		this.planilhas = this.session.find(this.planilhaDownload);
	}

	public void findByDate() {
		this.planilhas = this.session.find(this.inicio, this.fim);
	}

	@SuppressWarnings("deprecation")
	private String getData(Date data) {
		String d = String.valueOf(data.getDate());
		d = d + "-" + String.valueOf(data.getMonth() + 1);
		return d;
	}

	public void carregaLastPlanilha() {
		this.lastPlanilhas = this.session.findLast();
		int size = this.lastPlanilhas.size() - 1;
		if (size >= 2) {
			for (int i = size; i > 2; i--) {
				this.lastPlanilhas.remove(i);
			}
		}

	}
}
