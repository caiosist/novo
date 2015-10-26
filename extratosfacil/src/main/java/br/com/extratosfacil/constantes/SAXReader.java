package br.com.extratosfacil.constantes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.com.extratosfacil.entities.planilha.ItemPlanilhaUpload;

/**
 * Classe que faz o parser/leitura de um arquivo XML
 * 
 * @author Loiane Groner
 *
 */
public class SAXReader extends DefaultHandler {

	/** Buffer que guarda as informações quando um texto é encontrado */
	private StringBuffer valorAtual = new StringBuffer(50);

	private boolean emissao = false;

	private boolean pedagios = false;

	private boolean veiculos = false;

	int linha = 0;

	int coluna = 0;

	private ItemPlanilhaUpload item = new ItemPlanilhaUpload();

	private String dataEmissao = "";

	private List<ItemPlanilhaUpload> itens = new ArrayList<ItemPlanilhaUpload>();

	/**
	 * Constutor que inicializa os objetos necessários para fazer o parse do
	 * arquivo
	 * 
	 * @throws ParserCoxnfigurationException
	 * @throws SAXEception
	 * @throws IOException
	 */
	public List<ItemPlanilhaUpload> parse(String path)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		parser.parse(path, this);
		return this.itens;
	}

	/**
	 * Indica que o parser achou o início do documento XML. Este evento não lhe
	 * passa qualquer informação, apenas indica que o parser vai começar a
	 * escanear o arquivo XML.
	 */
	public void startDocument() {
		System.out.println("Iniciando a leitura do XML");
	}

	/**
	 * Indica que o parser achou e fim do documento XML.
	 */
	public void endDocument() {
		System.out.println("Acabou a leitura do XML");
	}

	/**
	 * Indica que o parser achou o início de uma tag (tag de abertura/início).
	 * Este evento fornece o nome do elemento, o nome e valor dos atributos
	 * deste elemento, e também pode fornecer as informações sobre o namespace.
	 */
	public void startElement(String uri, String localName, String tag,
			Attributes atributos) {

		// cria um contato
		if (tag.equalsIgnoreCase("Worksheet")) {

			if (pedagios) {
				veiculos = false;
			}

			for (int i = 0; i < atributos.getLength(); i++) {

				if (atributos.getQName(i).equalsIgnoreCase("ss:Name")) {
					if (atributos.getValue(i).equalsIgnoreCase(
							"PASSAGENS PEDÁGIO")) {
						System.out.println("Achei A sheet correta ==> "
								+ atributos.getValue(i));
						pedagios = true;
					}
				}
			}
		}

	}

	/**
	 * Indica que o parser achou o fim de uma tag/elemento. Este evento fornece
	 * o nome do elemento, e também pode fornecer as informações sobre o
	 * namespace.
	 */
	public void endElement(String uri, String localName, String tag) {

		if (emissao) {
			if (tag.equalsIgnoreCase("Data")) {
				this.setDataEmissao(valorAtual.toString().trim()
						.substring(0, 10));
				emissao = false;
			}
		} else if (tag.equalsIgnoreCase("Data") && !emissao) {
			if (valorAtual.toString().trim().equalsIgnoreCase("Vencimento")) {
				emissao = true;
			}
		}

		if (pedagios && tag.equalsIgnoreCase("Row")) {
			linha += 1;
			if (linha == 2) {
				// pedagios = false;
				veiculos = true;
			}
		}

		if (veiculos && tag.equalsIgnoreCase("Data")) {
			coluna += 1;
			try {
				criaVeiculo(coluna, valorAtual);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (coluna == 10) {
				this.itens.add(this.item);
				this.item = new ItemPlanilhaUpload();
				coluna = 0;
				// System.out.println("-----------------------");
			}
		}
		// limpa o valor Atual
		valorAtual.delete(0, valorAtual.length());
	}

	private void criaVeiculo(int col, StringBuffer val) throws ParseException {

		String conteudo = val.toString().trim();

		// formatar a data da planilha para o Obj
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Formatar a Hora da Planilha para o objeto
		SimpleDateFormat shf = new SimpleDateFormat("HH:mm:ss");

		if (col == 1) {
			conteudo = removeHifen(conteudo);
			item.setPlaca(conteudo);
		} else if (col == 5) {
			item.setCategoria(Integer.valueOf(conteudo));
		} else if (col == 6) {
			item.setData(sdf.parse(conteudo));
		} else if (col == 7) {
			item.setHora(shf.parse(conteudo));
		} else if (col == 8) {
			item.setConcessionaria(conteudo);
		} else if (col == 9) {
			item.setPraca(conteudo);
		} else if (col == 10) {
			item.setValor(Double.valueOf(conteudo));
		}
	}

	private static String removeHifen(String conteudo) {
		return conteudo.replaceAll("-", "");
	}

	/**
	 * Indica que o parser achou algum Texto (Informação).
	 */
	public void characters(char[] ch, int start, int length) {
		valorAtual.append(ch, start, length);
	}

	public String getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(String dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

}
