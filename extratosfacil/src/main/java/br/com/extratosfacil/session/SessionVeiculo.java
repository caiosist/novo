package br.com.extratosfacil.session;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import br.com.extratosfacil.constantes.Mensagem;
import br.com.extratosfacil.constantes.Sessao;
import br.com.extratosfacil.entities.Empresa;
import br.com.extratosfacil.entities.Veiculo;
import br.com.jbc.controller.Controller;

/**
 * Session que representa as regras de neg�cios da entidade Veiculo
 * 
 * @author Paulo Henrique da Silva
 * @since 29/07/2015
 * @version 1.0
 * @category Session
 */

public class SessionVeiculo {

	/*-------------------------------------------------------------------
	 * 		 					ATTRIBUTES
	 *-------------------------------------------------------------------*/

	private Controller<Veiculo> controller = new Controller<Veiculo>();

	/*-------------------------------------------------------------------
	 * 		 					GETTERS AND SETTERS
	 *-------------------------------------------------------------------*/

	public Controller<Veiculo> getController() {
		return controller;
	}

	public void setController(Controller<Veiculo> controller) {
		this.controller = controller;
	}

	/*-------------------------------------------------------------------
	 * 		 					METHODS
	 *-------------------------------------------------------------------*/

	public boolean save(Veiculo veiculo) {
		if (this.validaVeiculo(veiculo)) {
			try {
				this.controller.insert(veiculo);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean update(Veiculo veiculo) {
		if (this.validaVeiculo(veiculo)) {
			try {
				this.controller.update(veiculo);
				return true;
			} catch (ConstraintViolationException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean remove(Veiculo veiculo) throws Exception {
		this.controller.delete(veiculo);
		return true;
	}

	public boolean validaVeiculo(Veiculo veiculo) {
		Veiculo tempVeiculo;
		Empresa empresa = Sessao.getEmpresaSessao();
		try {
			tempVeiculo = this.controller
					.getObjectByHQLCondition("from Veiculo WHERE placaVeiculo = '"
							+ veiculo.getPlacaVeiculo().toUpperCase()
							+ "' AND empresa.id = '" + empresa.getId() + "'");
		} catch (Exception e) {
			tempVeiculo = null;
			e.printStackTrace();
		}

		if (veiculo.getCategoria() == null) {
			Mensagem.send(Mensagem.MSG_INCOMPLETO, Mensagem.ERROR);
			return false;
		} else if ((veiculo.getModeloVeiculo() == null)
				|| (veiculo.getModeloVeiculo().trim().equals(""))) {
			Mensagem.send(Mensagem.MSG_INCOMPLETO, Mensagem.ERROR);
			return false;
		} else if ((veiculo.getPlacaVeiculo() == null)
				|| (veiculo.getPlacaVeiculo().trim().equals(""))) {
			Mensagem.send(Mensagem.MSG_INCOMPLETO, Mensagem.ERROR);
			return false;
		} else if (tempVeiculo != null) {
			Mensagem.send(Mensagem.MSG_PLACA, Mensagem.ERROR);
			return false;
		} else {
			// deixa as placas em maiusculo
			veiculo.setPlacaVeiculo(veiculo.getPlacaVeiculo().toUpperCase());
			// capturar a empresa da sessao e setar no veiculo...
			veiculo.setEmpresa(Sessao.getEmpresaSessao());
		}

		return true;
	}

	public List<Veiculo> findList(Veiculo veiculo) {
		try {
			return controller.findList(veiculo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Veiculo find(Veiculo veiculo) {

		try {
			return this.controller.find(veiculo,
					Controller.SEARCH_EQUALS_STRING);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}