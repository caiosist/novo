package br.com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.com.extratosfacil.entities.Empresa;
import br.com.extratosfacil.entities.Veiculo;
import br.com.extratosfacil.entities.planilha.ItemPlanilhaDownload;
import br.com.jbc.controller.Controller;

public class ReadVeiculos extends DefaultHandler {

	/** Buffer que guarda as informações quando um texto é encontrado */
	private StringBuffer valorAtual = new StringBuffer(50);

	private boolean mensalidade = false;

	private Controller<Empresa> controllerEmpresa = new Controller<Empresa>();

	private boolean veiculos = false;

	int linha = 0;

	int coluna = 0;

	private Empresa empresa = new Empresa();

	private Veiculo item = new Veiculo();

	private List<Veiculo> itens = new ArrayList<Veiculo>();

	/**
	 * Constutor que inicializa os objetos necessários para fazer o parse do
	 * arquivo
	 * 
	 * @throws ParserCoxnfigurationException
	 * @throws SAXEception
	 * @throws IOException
	 */
	public List<Veiculo> parse(String path, Integer idEmpresa)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		parser.parse(path, this);

		this.setEmpresa(idEmpresa);

		return this.itens;
	}

	private void setEmpresa(Integer idEmpresa) {
		try {
			empresa = controllerEmpresa
					.getObjectByHQLCondition("from Empresa where id = "
							+ idEmpresa);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

			if (mensalidade) {
				veiculos = false;
			}

			for (int i = 0; i < atributos.getLength(); i++) {

				if (atributos.getQName(i).equalsIgnoreCase("ss:Name")) {
					if (atributos.getValue(i).equalsIgnoreCase("MENSALIDADES")) {
						System.out.println("Achei A sheet correta ==> "
								+ atributos.getValue(i));
						mensalidade = true;
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

		if (mensalidade && tag.equalsIgnoreCase("Row")) {
			linha += 1;
			if (linha == 1) {
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
			if (coluna == 6) {
				this.itens.add(this.item);
				this.item = new Veiculo();
				coluna = 0;
				// System.out.println("-----------------------");
			}
		}
		// limpa o valor Atual
		valorAtual.delete(0, valorAtual.length());
	}

	private void criaVeiculo(int col, StringBuffer val) throws ParseException {

		String conteudo = val.toString().trim();

		if (col == 1) {
			conteudo = removeHifen(conteudo);
			item.setPlacaVeiculo(conteudo);
		} else if (col == 4) {
			item.setCategoria(Integer.valueOf(conteudo));
		} else if (col == 6) {
			item.setEmpresa(this.empresa);
			item.setModeloVeiculo("VOLVO");
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

	public static void main(String[] args) {
		ReadVeiculos reader = new ReadVeiculos();
		String path = "D:\\Documentos\\Enterprises\\Extrato Facil\\Correcoes\\TransFalls\\Correcao\\2015";

		List<Veiculo> temp = new ArrayList<Veiculo>();

		List<Veiculo> veiculos = new ArrayList<Veiculo>();

		for (int i = 1; i <= 12; i++) {
			try {
				temp = null;
				temp = reader.parse(path + File.separator + i + ".xls", 01);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (temp != null) {
				for (int j = 0; j < temp.size(); j++) {
					if (!existe(temp.get(j), veiculos)) {
						veiculos.add(temp.get(j));
					}
				}
			}

		}

		criaExecel(veiculos, "Lista de Veiculos", path);

	}

	private static boolean existe(Veiculo veiculo, List<Veiculo> veiculos) {

		for (int i = 0; i < veiculos.size(); i++) {
			if (veiculos.get(i).getPlacaVeiculo()
					.equals(veiculo.getPlacaVeiculo())) {
				return true;
			}
		}
		return false;
	}

	public static void criaExecel(List<Veiculo> itens, String descricao,
			String caminho) {

		caminho += File.separator + descricao + ".xls";

		// Cria um Arquivo Excel
		Workbook wb = new HSSFWorkbook();

		int row = 0;

		// Cria uma planilha Excel
		Sheet sheet = wb.createSheet("Veiculos");

		// Cria uma linha na Planilha.
		Row cabecalho = sheet.createRow((short) row);

		// Cria as células na linha
		cabecalho.createCell(0).setCellValue("Placa");
		cabecalho.createCell(1).setCellValue("Categoria");

		int width = sheet.getColumnWidth(0) * 2;
		sheet.setColumnWidth(0, width);
		sheet.setColumnWidth(1, width);

		Iterator<Row> rowIterator = sheet.iterator();
		Row r = rowIterator.next();

		if (r.getRowNum() == 0) {
			CellStyle style = wb.createCellStyle();// Create style
			Font font = wb.createFont();// Create font
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);// Make font bold
			style.setFont(font);// set it to bold

			for (int k = 0; k < r.getLastCellNum(); k++) {// For each cell
															// in
															// the row
				r.getCell(k).setCellStyle(style);// Set the style
			}
		}
		for (int i = 0; i < itens.size(); i++) {
			Veiculo item = itens.get(i);
			row = row + 1;

			Row dados = sheet.createRow(row);

			dados.createCell(0).setCellValue(item.getPlacaVeiculo());
			dados.createCell(1).setCellValue(item.getCategoria());
		}

		try (FileOutputStream fileOut = new FileOutputStream(caminho)) {
			wb.write(fileOut);
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
