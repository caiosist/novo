package br.com.extratosfacil.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Classe que representa um Plano
 *
 * @author Caio Cesar Correia
 * @since 28/07/2015
 * @version 1.0
 * @category Entity
 */

@Entity
@Table(name = "TBL_PLANO")
public class Plano implements Serializable {

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
	@Column(length = 50, nullable = false)
	private String nomePlano;

	/**
	 * 
	 */
	@Column(nullable = false)
	private Float valorPlano;

	/**
	 * 
	 */
	@Column(nullable = false)
	private Float credito;

	/**
	 * 
	 */
	@Column(nullable = false)
	private Long quantidadeVeiculos;

	/**
	 * 
	 */
	@Column(length = 255, nullable = false)
	private String status;

	/**
	 * 
	 */
	@Column
	@Temporal(TemporalType.DATE)
	private Date vencimento;

	/**
	 * Numero de meses do plano
	 */
	@Column
	private Integer periodo;

	/*-------------------------------------------------------------------
	 *				 		     CONSTRUCTORS
	 *-------------------------------------------------------------------*/

	public Plano() {
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

	public Date getVencimento() {
		return vencimento;
	}

	public Float getCredito() {
		return credito;
	}

	public void setCredito(Float credito) {
		this.credito = credito;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public void setVencimento(Date vencimento) {
		this.vencimento = vencimento;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getNomePlano() {
		return nomePlano;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setNomePlano(String nomePlano) {
		this.nomePlano = nomePlano;
	}

	public Float getValorPlano() {
		return valorPlano;
	}

	public void setValorPlano(Float valorPlano) {
		this.valorPlano = valorPlano;
	}

	public Long getQuantidadeVeiculos() {
		return quantidadeVeiculos;
	}

	public void setQuantidadeVeiculos(Long quantidadeVeiculos) {
		this.quantidadeVeiculos = quantidadeVeiculos;
	}

	@Override
	public String toString() {
		return this.getNomePlano();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((credito == null) ? 0 : credito.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((nomePlano == null) ? 0 : nomePlano.hashCode());
		result = prime * result + ((periodo == null) ? 0 : periodo.hashCode());
		result = prime
				* result
				+ ((quantidadeVeiculos == null) ? 0 : quantidadeVeiculos
						.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((valorPlano == null) ? 0 : valorPlano.hashCode());
		result = prime * result
				+ ((vencimento == null) ? 0 : vencimento.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Plano))
			return false;
		Plano other = (Plano) obj;
		if (credito == null) {
			if (other.credito != null)
				return false;
		} else if (!credito.equals(other.credito))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nomePlano == null) {
			if (other.nomePlano != null)
				return false;
		} else if (!nomePlano.equals(other.nomePlano))
			return false;
		if (periodo == null) {
			if (other.periodo != null)
				return false;
		} else if (!periodo.equals(other.periodo))
			return false;
		if (quantidadeVeiculos == null) {
			if (other.quantidadeVeiculos != null)
				return false;
		} else if (!quantidadeVeiculos.equals(other.quantidadeVeiculos))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (valorPlano == null) {
			if (other.valorPlano != null)
				return false;
		} else if (!valorPlano.equals(other.valorPlano))
			return false;
		if (vencimento == null) {
			if (other.vencimento != null)
				return false;
		} else if (!vencimento.equals(other.vencimento))
			return false;
		return true;
	}

	public void clean() {
		this.credito = null;
		this.id = null;
		this.nomePlano = "";
		this.periodo = null;
		this.quantidadeVeiculos = null;
		this.status = "";
		this.status = "";
		this.valorPlano = null;
		this.vencimento = null;
	}

}