package br.com.extratosfacil.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.SAXException;

import br.com.extratosfacil.constantes.SAXReader;
import br.com.extratosfacil.entities.Veiculo;
import br.com.extratosfacil.entities.planilha.ItemPlanilhaDownload;
import br.com.extratosfacil.entities.planilha.ItemPlanilhaUpload;

/**
 * Session que representa as regras de negÃ³cios da entidade Empresa
 * 
 * @author Paulo Henrique da Silva
 * @since 29/07/2015
 * @version 1.0
 * @category Session
 */

public class SessionTeste {

	/*-------------------------------------------------------------------
	 * 		 					ATTRIBUTES
	 *-------------------------------------------------------------------*/

	private int erros;

	/*-------------------------------------------------------------------
	 * 		 					GETTERS AND SETTERS
	 *-------------------------------------------------------------------*/

	public int getErros() {
		return erros;
	}

	public void setErros(int erros) {
		this.erros = erros;
	}

	/*-------------------------------------------------------------------
	 * 		 					METHODS
	 *-------------------------------------------------------------------*/

	public List<ItemPlanilhaDownload> carregaPlanilha(Object workbook,
			Veiculo veiculo) throws IOException, ParseException {

		if (workbook instanceof XSSFWorkbook) {
			XSSFSheet sheet = null;
			return this.lerPlanilha(workbook, sheet, veiculo);
		} else {
			HSSFSheet sheet = null;
			return this.lerPlanilha(workbook, sheet, veiculo);
		}
	}

	private List<ItemPlanilhaDownload> lerPlanilha(Object workbook,
			Object sheet, Veiculo veiculo) throws IOException, ParseException {

		// formatar a data da planilha para o Obj
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Formatar a Hora da Planilha para o objeto
		SimpleDateFormat shf = new SimpleDateFormat("HH:mm:ss");

		// Lista de veiculos da planilha
		List<ItemPlanilhaUpload> lista = new ArrayList<ItemPlanilhaUpload>();

		ItemPlanilhaUpload item = new ItemPlanilhaUpload();

		Iterator<Row> rowIterator = null;

		if (workbook instanceof XSSFWorkbook) {
			sheet = ((XSSFWorkbook) workbook).getSheet("Passagens de Pedágio");
			rowIterator = ((XSSFSheet) sheet).iterator();
		} else {
			sheet = ((HSSFWorkbook) workbook).getSheet("Passagens de Pedágio");
			// retorna todas as linhas da planilha 0 (aba 1)
			rowIterator = ((HSSFSheet) sheet).iterator();
		}

		// varre todas as linhas da planilha 0
		while (rowIterator.hasNext()) {

			// recebe cada linha da planilha
			Row row = rowIterator.next();

			if (row.getRowNum() == 0) {
				continue;
			}

			// pegamos todas as celulas desta linha
			Iterator<Cell> cellIterator = row.iterator();

			// varremos todas as celulas da linha atual
			while (cellIterator.hasNext()) {

				// criamos uma celula
				Cell cell = cellIterator.next();

				if (cell.getColumnIndex() == 0) {
					item.setPlaca(cell.getStringCellValue());
				} else if (cell.getColumnIndex() == 2) {
					item.setCategoria(Integer.valueOf(cell.getStringCellValue()));
				} else if (cell.getColumnIndex() == 3) {

					item.setData(sdf.parse(cell.getStringCellValue()));

				} else if (cell.getColumnIndex() == 4) {

					item.setHora(shf.parse(cell.getStringCellValue()));

				} else if (cell.getColumnIndex() == 5) {
					item.setConcessionaria(cell.getStringCellValue());
				} else if (cell.getColumnIndex() == 6) {
					item.setPraca(cell.getStringCellValue());
				}
				if (cell.getColumnIndex() == 7) {
					if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						item.setValor(cell.getNumericCellValue());
					} else {
						item.setValor(0.0);
					}
					lista.add(item);
					item = new ItemPlanilhaUpload();
				}

			}

		}

		if (workbook instanceof XSSFWorkbook) {
			((XSSFWorkbook) workbook).close();
		} else {
			((HSSFWorkbook) workbook).close();
		}
		return this.makeTheMagic(lista, veiculo);
	}

	public List<ItemPlanilhaDownload> makeTheMagic(
			List<ItemPlanilhaUpload> itensPlanilha, Veiculo veiculo) {
		// fazemos a comparacao dos veiculos da planilha upload com os
		// cadastrados

		// Lista de veiculos com cobrancas incorretas
		List<ItemPlanilhaDownload> itensIncorretos = new ArrayList<ItemPlanilhaDownload>();

		erros = 0;
		for (int j = 0; j < itensPlanilha.size(); j++) {
			if (veiculo.getPlacaVeiculo().equalsIgnoreCase(
					itensPlanilha.get(j).getPlaca())
					&& itensPlanilha.get(j).getValor() > 0.0) {
				if (isDuplicado(itensPlanilha, j)) {
					if (itensIncorretos.size() < 5) {
						itensIncorretos.add(this.criaItemDownload(
								itensPlanilha.get(j), veiculo, true));
					}
					erros++;
				} else {
					if ((itensPlanilha.get(j).getCategoria() > veiculo
							.getCategoria())) {
						erros++;
						if (itensIncorretos.size() < 5) {
							itensIncorretos.add(this.criaItemDownload(
									itensPlanilha.get(j), veiculo, false));
						}

					}
				}
			}
		}

		return itensIncorretos;
	}

	private boolean isDuplicado(List<ItemPlanilhaUpload> itensPlanilha, int i) {
		if (i > 0) {
			int j = i - 1;
			if (itensPlanilha.get(i).getPlaca()
					.equalsIgnoreCase(itensPlanilha.get(j).getPlaca())) {
				if (itensPlanilha.get(i).getPraca()
						.equalsIgnoreCase(itensPlanilha.get(j).getPraca())) {
					if (itensPlanilha.get(i).getData().getTime() == itensPlanilha
							.get(j).getData().getTime()) {
						if (itensPlanilha.get(i).getHora().getTime() == itensPlanilha
								.get(j).getHora().getTime()) {
							return true;
						}
					}
				}

			}
		}
		return false;
	}

	private ItemPlanilhaDownload criaItemDownload(
			ItemPlanilhaUpload itemPlanilhaUpload, Veiculo temp,
			boolean duplicado) {

		ItemPlanilhaDownload item = new ItemPlanilhaDownload();
		item.setCategoria(itemPlanilhaUpload.getCategoria());
		item.setCategoriaCorreta(temp.getCategoria());
		item.setConcessionaria(itemPlanilhaUpload.getConcessionaria());
		item.setData(itemPlanilhaUpload.getData());
		item.setHora(itemPlanilhaUpload.getHora());
		item.setPlaca(itemPlanilhaUpload.getPlaca());
		item.setValor(itemPlanilhaUpload.getValor());
		item.setPraca(itemPlanilhaUpload.getPraca());
		item.setValorCorreto(item.getValor()
				/ item.formataCategoria(item.getCategoria())
				* item.formataCategoria(item.getCategoriaCorreta()));
		if (duplicado) {
			item.setValorRestituicao(item.getValor());
		} else {
			item.setValorRestituicao(item.getValor() - item.getValorCorreto());
		}
		item.setObs(duplicado ? "Passagem Duplicada"
				: "Número de Eixos incorreto");
		return item;

	}

	public Object validaPlanilha(String path, Object workbook, Object sheet) {

		FileInputStream fisPlanilha = null;

		File file = new File(path);
		try {
			fisPlanilha = new FileInputStream(file);

			if (workbook instanceof XSSFWorkbook) {
				workbook = new XSSFWorkbook(fisPlanilha);
				sheet = ((XSSFWorkbook) workbook)
						.getSheet("Passagens de Pedágio");
			}
			if (workbook instanceof HSSFWorkbook) {
				workbook = new HSSFWorkbook(fisPlanilha);
				sheet = ((HSSFWorkbook) workbook)
						.getSheet("Passagens de Pedágio");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if (sheet == null) {
			return null;
		}

		return workbook;
	}

	public Object validaPlanilha(String path, boolean xlsx) throws IOException {
		if (xlsx) {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = null;
			return validaPlanilha(path, workbook, sheet);
		} else {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = null;
			return validaPlanilha(path, workbook, sheet);
		}

	}

	public List<ItemPlanilhaDownload> lerXml(String path, Veiculo veiculo)
			throws ParserConfigurationException, SAXException, IOException {

		List<ItemPlanilhaUpload> lista = new ArrayList<ItemPlanilhaUpload>();

		SAXReader reader = new SAXReader();

		lista.addAll(reader.parse(path));

		return this.makeTheMagic(lista, veiculo);

	}

	public boolean validaXml(String caminho) {
		return true;
	}

	public List<ItemPlanilhaDownload> carregaXml(String path, Veiculo veiculo)
			throws ParserConfigurationException, SAXException, IOException {
		return this.lerXml(path, veiculo);
	}
}
