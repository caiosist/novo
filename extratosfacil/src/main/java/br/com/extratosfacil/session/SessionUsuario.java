package br.com.extratosfacil.session;

import br.com.extratosfacil.entities.Empresa;
import br.com.jbc.controller.Controller;

public class SessionUsuario {

	private Controller<Empresa> controller = new Controller<Empresa>();

	public Empresa efetuarLogin(Empresa empresa) {
		empresa.setSenha(this.crip(empresa.getSenha()));
		try {
			return controller.find(empresa, Controller.SEARCH_EQUALS_STRING);
		} catch (Exception e) {
			e.printStackTrace();
			controller = null;
			return null;
		}
	}

	public void clean() {
		// TODO Auto-generated method stub

	}

	public String crip(String senha) {
		char[] crip = new char[senha.length()];
		char c;
		int charVal = 0;
		for (int i = 0; i < senha.length(); i++) {
			c = senha.charAt(i);
			charVal = c + 1;
			c = (char) charVal;
			crip[i] = c;
		}
		return String.valueOf(crip);
	}
}
