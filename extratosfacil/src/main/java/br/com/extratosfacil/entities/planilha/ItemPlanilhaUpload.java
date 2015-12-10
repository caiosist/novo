package br.com.extratosfacil.entities.planilha;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.extratosfacil.entities.Empresa;

/**
 * Classe que representa os itens da planilha de upload
 *
 * @author Paulo Henrique da Silva
 * @since 29/07/2015
 * @version 1.0
 * @category Entity
 */

@Entity
@Table(name = "TBL_PASSAGENS")
public class ItemPlanilhaUpload implements Serializable,
		Comparable<ItemPlanilhaUpload> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*-------------------------------------------------------------------
	 *				 		     ATTRIBUTES
	 *-------------------------------------------------------------------*/

	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 
	 */
	@Column(length = 8, nullable = false)
	private String placa;

	/**
	 * 
	 */
	@Column(nullable = false)
	private Integer categoria;

	/**
	 * 
	 */
	@Column
	@Temporal(TemporalType.DATE)
	private Date data;

	/**
	 * 
	 */
	@Column
	@Temporal(TemporalType.DATE)
	private Date dataEmissao;

	/**
	 * 
	 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date hora;

	/**
	 * 
	 */
	@Column(length = 100, nullable = false)
	private String concessionaria;

	/**
	 * 
	 */
	@Column(length = 100, nullable = false)
	private String praca;

	/**
	 * 
	 */
	@Column
	private Double valor;

	/**
	 * 
	 */
	@ManyToOne
	private Empresa empresa;

	/*-------------------------------------------------------------------
	 *				 		     CONSTRUCTORS
	 *-------------------------------------------------------------------*/

	public ItemPlanilhaUpload() {
	}

	/*-------------------------------------------------------------------
	 *				 		     GETTERS AND SETTERS
	 *-------------------------------------------------------------------*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getValor() {
		return valor;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public Integer getCategoria() {
		return categoria;
	}

	public void setCategoria(Integer categoria) {
		this.categoria = categoria;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public String getConcessionaria() {
		return concessionaria;
	}

	public void setConcessionaria(String concessionaria) {
		this.concessionaria = concessionaria;
	}

	public String getPraca() {
		return praca;
	}

	public void setPraca(String praca) {
		this.praca = praca;
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int compareTo(ItemPlanilhaUpload o) {
		// TODO Auto-generated method stub
		return this.getPlaca().compareTo(o.getPlaca());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoria == null) ? 0 : categoria.hashCode());
		result = prime * result
				+ ((concessionaria == null) ? 0 : concessionaria.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((hora == null) ? 0 : hora.hashCode());
		result = prime * result + ((placa == null) ? 0 : placa.hashCode());
		result = prime * result + ((praca == null) ? 0 : praca.hashCode());
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemPlanilhaUpload other = (ItemPlanilhaUpload) obj;
		if (categoria == null) {
			if (other.categoria != null)
				return false;
		} else if (!categoria.equals(other.categoria))
			return false;
		if (concessionaria == null) {
			if (other.concessionaria != null)
				return false;
		} else if (!concessionaria.equals(other.concessionaria))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (hora == null) {
			if (other.hora != null)
				return false;
		} else if (!hora.equals(other.hora))
			return false;
		if (placa == null) {
			if (other.placa != null)
				return false;
		} else if (!placa.equals(other.placa))
			return false;
		if (praca == null) {
			if (other.praca != null)
				return false;
		} else if (!praca.equals(other.praca))
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		return true;
	}

}