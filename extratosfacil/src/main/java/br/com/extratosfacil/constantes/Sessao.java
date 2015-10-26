package br.com.extratosfacil.constantes;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.mail.EmailException;

import br.com.extratosfacil.entities.Email;
import br.com.extratosfacil.entities.Empresa;

public class Sessao {

	public static Empresa getEmpresaSessao() {
		return (Empresa) FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("empresa");
	}

	public static void setEmpresaSessao(Empresa empresa) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.put("empresa", empresa);
	}

	public static void redireciona(String pagina) {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("/" + pagina);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void redirect(String page) {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		try {
			externalContext.redirect(page);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void enviarEmailConfirmaCompra(String email, String nome){
		
		try {
			Email.sendEmail(email, nome, "Pagamento Confirmado", "Seu Pagamento foi confirmado, acesse agora mesmo o sistema Extratos Facil", "http://extratosfacil.com.br");
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}