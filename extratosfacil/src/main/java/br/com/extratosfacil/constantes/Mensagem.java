package br.com.extratosfacil.constantes;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class Mensagem {

	public static final String MSG_DATAPLANILHA = "N�o � poss�vel enviar planilhas antigas, caso deseje fazer a confer�ncia dessas planilhas, entre em contato por e-mail: contato@extratosfacil.com.br";

	public static String EMAIL_ENVIADO = "E-mail enviado com sucesso.";

	public static String MSG_ACEITO = "Voc� precisa aceitar os termos.";

	public static String MSG_SALVA = "Os dados foram salvos com sucesso.";

	public static String MSG_REMOVE = "Os dados foram exclu�dos com sucesso.";

	public static String MSG_USER_NAO_CONFIRMADO = "Conta aguardando confirma��o. Por favor, verifique o seu e-mail.";

	public static String MSG_NOT_REMOVE = "N�o foi poss�vel excluir.";

	public static String MSG_UPDATE = "Os dados foram atualizados com sucesso.";

	public static String MSG_UPLOAD = "Upload realizado com sucesso! Clique em \"Download\" para baixar a planilha com as corre��es.";

	public static String MSG_INCOMPLETO = "Por favor, preencha todos os campos.";

	public static String MSG_CNPJ = "CNPJ inv�lido.";
	
	public static String MSG_CPF = "CPF inv�lido.";

	public static String MSG_EMAIL = "E-mail inv�lido ou j� cadastrado.";
	
	public static String MSG_EMAIL_ENVIADO = "E-mail enviado.";

	public static String MSG_USER_NAO_ENCONTRADO = "Usu�rio inv�lido.";

	public static String MSG_PLACA = "J� existe um ve�culo cadastrado com esta placa.";

	public static String MSG_LOGIN = "Login j� existente, por favor escolha um login diferente.";

	public static String MSG_CNPJ_UNIQUE = "J� existe uma empresa cadastrada com este CNPJ.";
	
	public static String MSG_CPF_UNIQUE = "J� existe um usu�rio cadastrado com este CPF.";

	public static String MSG_RAZAO_SOCIAL = "J� existe uma empresa cadastrada com esta raz�o social.";

	public static String MSG_PLANILHA_ERRADA = "A planilha enviada est� incorreta ou foi modificada. Por favor, selecione a planilha correta.";

	public static String MSG_EMPRESA_INVALIDA = "A planilha enviada n�o pertence a empresa cadastrada. Por favor, selecione a planilha correta.";

	public static String MSG_CONF_SENHA = "O campo confirmar senha n�o corresponde a senha digitada.";

	public static String MSG_EMAIL_INVALIDO = "O e-mail informado n�o est� cadastrado em nossa base de dados.";

	public static String MSG_QUANTIDADE_INVALIDA = "O n�mero de ve�culos n�o pode ser 0 (Zero).";

	public static String MSG_PERIODO = "Selecione um per�odo de pagamento.";

	public static String MSG_VALOR_PLANO = "N�o � poss�vel alterar um plano com o pagamento efetuado, por outro plano com valor inferior. Aguarde o ciclo atual de seu plano terminar para realizar esta altera��o.";

	public static Severity INFO = FacesMessage.SEVERITY_INFO;

	public static Severity ERROR = FacesMessage.SEVERITY_ERROR;

	// Mostrar Mensagem ao ususario
	public static void send(String msg, Severity tipo) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(tipo, msg, ""));
	}
}