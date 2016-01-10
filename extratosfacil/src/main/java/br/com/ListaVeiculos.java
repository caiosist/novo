package br.com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import br.com.extratosfacil.constantes.SAXReader;
import br.com.extratosfacil.entities.Empresa;
import br.com.extratosfacil.entities.Veiculo;
import br.com.extratosfacil.entities.planilha.ItemPlanilhaDownload;
import br.com.extratosfacil.entities.planilha.ItemPlanilhaUpload;
import br.com.extratosfacil.entities.planilha.PlanilhaUpload;
import br.com.jbc.controller.Controller;

public class ListaVeiculos {

	public static String caminho = "D:\\Documentos\\Enterprises\\Extrato Facil\\Correcoes\\BrasilCar\\Extratos\\2015";
	public static int idEmpresa = 10;

	public static String dataEmissao;
	public static String mes = "00";
	public static Controller<Veiculo> controllerVeiculo = new Controller<Veiculo>();
	private static Controller<ItemPlanilhaUpload> controllerItens = new Controller<ItemPlanilhaUpload>();
	private static Controller<PlanilhaUpload> controller = new Controller<PlanilhaUpload>();
	private static Controller<Empresa> controllerEmpresa = new Controller<Empresa>();
	private static Date dataEmissaoFormatada = null;

	private static PlanilhaUpload planilhaUpload = new PlanilhaUpload();

	static List<List<ItemPlanilhaDownload>> itensDuplicados = new ArrayList<List<ItemPlanilhaDownload>>();
	static List<List<ItemPlanilhaDownload>> itensPagos = new ArrayList<List<ItemPlanilhaDownload>>();
	static List<List<ItemPlanilhaDownload>> itensDataIncorreta = new ArrayList<List<ItemPlanilhaDownload>>();
	static List<List<ItemPlanilhaDownload>> itensEixoIncorreto = new ArrayList<List<ItemPlanilhaDownload>>();

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	static SimpleDateFormat sdfXls = new SimpleDateFormat("dd/MM/yyyy");

	static Empresa empresa = new Empresa();

	public static Date getEmissaoFormatada() {
		if (dataEmissaoFormatada == null) {
			try {
				return sdf.parse(dataEmissao);
			} catch (ParseException e) {
				try {
					return sdfXls.parse(dataEmissao);
				} catch (Exception e2) {
					// TODO: handle exception
				}
				e.printStackTrace();
			}
			return null;
		} else {
			return dataEmissaoFormatada;
		}
	}

	public static void main(String[] args) {

		for (int i = 1; i <= 12; i++) {
			String planilha = caminho + File.separator + i + ".xls";

			System.out.println("");
			System.out.println("Comecou a Ler - planilha mes " + (Integer.parseInt(mes)+1));
			
			try {
				lerXls(planilha);
			} catch (Exception e) {
				// e.printStackTrace();
				lerXml(planilha);
			}

			planilhaUpload.setData(new Date());
			planilhaUpload.setEmissao(getEmissaoFormatada());
			planilhaUpload.setEmpresa(empresa);
			planilhaUpload.setPath(planilha);

			try {
				controller.insert(planilhaUpload);
			} catch (Exception e) {
				// TODO: handle exception
			}
			System.out.println("");
			System.out.println("Fim da Planilha do Mes - " + mes);
		}

		criaExecel(itensDataIncorreta, "Verificar Data", caminho);
		criaExecel(itensDuplicados, "Passagens Duplicadas", caminho);
		criaExecel(itensPagos, "Passagens já Cobradas", caminho);
		criaExecel(itensEixoIncorreto, "Numero de Eixos Incorreto", caminho);
	}

	private static void lerXls(String caminho) throws Exception {

		// formatar a data da planilha para o Obj
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Formatar a Hora da Planilha para o objeto
		SimpleDateFormat shf = new SimpleDateFormat("HH:mm:ss");

		// Lista de veiculos da planilha
		List<ItemPlanilhaUpload> lista = new ArrayList<ItemPlanilhaUpload>();

		ItemPlanilhaUpload item = new ItemPlanilhaUpload();

		HSSFWorkbook workbook = null;

		FileInputStream fisPlanilha = null;

		File file = new File(caminho);
		fisPlanilha = new FileInputStream(file);

		workbook = new HSSFWorkbook(fisPlanilha);
		HSSFSheet sheet = workbook.getSheet("Passagens de Pedágio");

		Iterator<Row> rowIterator = sheet.iterator();

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
					try {
						item.setData(sdf.parse(cell.getStringCellValue()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (cell.getColumnIndex() == 4) {
					try {
						item.setHora(shf.parse(cell.getStringCellValue()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					if (item.getValor() > 0) {
						lista.add(item);
					}
					item = new ItemPlanilhaUpload();
				}

			}

		}
		getDataEmissao(workbook);
		try {
			workbook.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		makeTheMagic(lista, true);

	}

	public static void getDataEmissao(HSSFWorkbook workbook) {
		Sheet sheet = workbook.getSheet("Resumo da Fatura");
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			if (row.getRowNum() < 3) {
				continue;
			}

			Iterator<Cell> cellIterator = row.iterator();
			while (cellIterator.hasNext()) {

				Cell cell = cellIterator.next();

				if ((cell.getRowIndex() == 3) && (cell.getColumnIndex() == 1)) {
					if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
						mes = cell.getStringCellValue().substring(3, 5);
						dataEmissao = cell.getStringCellValue();
						break;
					}
				}
			}
		}
	}

	public static void lerXml(String path) {

		List<ItemPlanilhaUpload> lista = new ArrayList<ItemPlanilhaUpload>();

		SAXReader reader = new SAXReader();
		try {
			lista.addAll(reader.parse(path));
			dataEmissao = reader.getDataEmissao();
			mes = dataEmissao.substring(5, 7);
		} catch (Exception e) {

			e.printStackTrace();
		}

		makeTheMagic(lista, false);

	}

	public static void makeTheMagic(List<ItemPlanilhaUpload> itensPlanilha,
			boolean xls) {

		List<ItemPlanilhaDownload> itensPagosMes = new ArrayList<ItemPlanilhaDownload>();
		List<ItemPlanilhaDownload> itensDataMes = new ArrayList<ItemPlanilhaDownload>();
		List<ItemPlanilhaDownload> itensEixoMes = new ArrayList<ItemPlanilhaDownload>();
		List<ItemPlanilhaDownload> itensDuplicadoMes = new ArrayList<ItemPlanilhaDownload>();

		List<ItemPlanilhaUpload> itensRemovidos = new ArrayList<ItemPlanilhaUpload>();

		// Cria um veiculo temporï¿½rio
		Veiculo temp = new Veiculo();

		// Setar a empresa no veiculo para a busca, no momento esta sendo um
		// objeto vazio, posteriormente pegaremos a empresa da sessao do
		// usuario
		temp.setEmpresa(empresa);
		// Buscar todos os veiculos da empresa
		List<Veiculo> veiculos = new ArrayList<Veiculo>();
		HashMap<String, Veiculo> mapaVeiculos = new HashMap<String, Veiculo>();

		try {
			veiculos = controllerVeiculo
					.getListByHQLCondition("from Veiculo where empresa_id = "
							+ temp.getEmpresa().getId());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		for (Veiculo veiculo : veiculos) {
			mapaVeiculos.put(veiculo.getPlacaVeiculo(), veiculo);
		}

		Collections.sort(itensPlanilha);
		System.out.println("");
		System.out.println("Procurando Erros...");

		for (ItemPlanilhaUpload itemPlanilha : itensPlanilha) {
			System.out.print(".");
			if (temp.getPlacaVeiculo() == null) {
				temp = mapaVeiculos.get(itemPlanilha.getPlaca());
				if (temp == null) {
					temp = new Veiculo();
					continue;
				}
			}

			temp = temp.getPlacaVeiculo().equals(itemPlanilha.getPlaca()) ? temp
					: mapaVeiculos.get(itemPlanilha.getPlaca());

			if (temp == null) {
				temp = new Veiculo();
				continue;
			}

			if (isPassagenPaga(itemPlanilha)) {
				itensRemovidos.add(itemPlanilha);
				itensPagosMes.add(criaItemDownload(itemPlanilha, temp, false,
						true, true));
				continue;
			}

			if (isDataIncorreta(itemPlanilha, xls)) {

				itensDataMes.add(criaItemDownload(itemPlanilha, temp, false,
						true, false));
			}

			if (isDuplicado(itensPlanilha, itemPlanilha)) {
				itensDuplicadoMes.add(criaItemDownload(itemPlanilha, temp,
						true, false, false));
				itensRemovidos.add(itemPlanilha);
			} else {
				if (itemPlanilha.getCategoria() > temp.getCategoria()) {
					itensEixoMes.add(criaItemDownload(itemPlanilha, temp,
							false, false, false));
				}

			}
		}

		itensDataIncorreta.add(itensDataMes);
		itensPagos.add(itensPagosMes);
		itensEixoIncorreto.add(itensEixoMes);
		itensDuplicados.add(itensDuplicadoMes);

		removeItens(itensRemovidos, itensPlanilha);
		persisteItens(itensPlanilha);

		return;
	}

	private static void removeItens(List<ItemPlanilhaUpload> itensRemovidos,
			List<ItemPlanilhaUpload> itensPlanilha) {
		for (int i = 0; i < itensRemovidos.size(); i++) {
			int x = itensPlanilha.indexOf(itensRemovidos.get(i));
			if (x >= 0) {
				itensPlanilha.remove(x);
			}
		}

	}

	private static boolean isPassagenPaga(ItemPlanilhaUpload itemIncorreto) {

		ItemPlanilhaUpload item = null;

		String query = "from ItemPlanilhaUpload where placa='"
				+ itemIncorreto.getPlaca() + "' and empresa_id = "
				+ empresa.getId() + " and categoria='"
				+ itemIncorreto.getCategoria() + "' and praca='"
				+ itemIncorreto.getPraca() + "' and hora='"
				+ itemIncorreto.getHora() + "' and data='"
				+ itemIncorreto.getData() + "'";

		try {
			item = controllerItens.getObjectByHQLCondition(query);
		} catch (Exception e) {
			// e.printStackTrace();
			// return true;
		}

		if (item == null) {
			return false;
		} else {

			if (item.getDataEmissao().equals(getEmissaoFormatada())) {
				return false;
			}

			itemIncorreto.setDataEmissao(item.getDataEmissao());
			return true;
		}

	}

	private static boolean isDataIncorreta(ItemPlanilhaUpload itemPlanilha,
			boolean xls) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		if (xls) {
			dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		}

		LocalDate dataExtrato = LocalDate.parse(dataEmissao, dtf);
		LocalDate dataCobranca = itemPlanilha.getData().toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate();

		Period p = Period.between(dataCobranca, dataExtrato);

		// numero de dias entre as datas
		int x = p.getDays() + (p.getMonths() * 30);

		if (x > 90) {
			return true;
		} else {
			return false;
		}

	}

	private static boolean isDuplicado(List<ItemPlanilhaUpload> itensPlanilha,
			ItemPlanilhaUpload itemPlanilha) {

		int i = itensPlanilha.indexOf(itemPlanilha);

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

	public static void persisteItens(List<ItemPlanilhaUpload> itensPlanilha) {

		Date emissao = null;

		// Verifica se existe essa planilha Salva no banco
		PlanilhaUpload planilha = new PlanilhaUpload();

		planilha.setEmissao(getEmissaoFormatada());
		emissao = getEmissaoFormatada();
		planilha.setEmpresa(empresa);

		try {
			planilha = controller.find(planilha);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Se essa planilha ainda nao existe salva todas as passagens no banco
		if (planilha == null) {
			System.out.println("");
			System.out.println("Salvando Passagem...");
			for (ItemPlanilhaUpload itemPlanilhaUpload : itensPlanilha) {
				System.out.print(".");
				itemPlanilhaUpload.setEmpresa(empresa);
				itemPlanilhaUpload.setDataEmissao(emissao);

				try {
					controllerItens.insert(itemPlanilhaUpload);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static ItemPlanilhaDownload criaItemDownload(
			ItemPlanilhaUpload itemPlanilhaUpload, Veiculo temp,
			boolean duplicado, boolean dataIncorreta, boolean passagemPaga) {

		ItemPlanilhaDownload item = new ItemPlanilhaDownload();

		item.setCategoria(itemPlanilhaUpload.getCategoria());
		item.setCategoriaCorreta(temp.getCategoria());
		item.setConcessionaria(itemPlanilhaUpload.getConcessionaria());
		item.setData(itemPlanilhaUpload.getData());
		item.setHora(itemPlanilhaUpload.getHora());
		item.setPlaca(itemPlanilhaUpload.getPlaca());
		item.setPraca(itemPlanilhaUpload.getPraca());
		item.setValor(itemPlanilhaUpload.getValor());
		item.setValorCorreto(item.getValor()
				/ item.formataCategoria(item.getCategoria())
				* item.formataCategoria(item.getCategoriaCorreta()));
		if (duplicado || dataIncorreta) {
			item.setValorRestituicao(item.getValor());
			item.setValorCorreto(0.0);
		} else {
			item.setValorRestituicao(item.getValor() - item.getValorCorreto());
		}
		if (!dataIncorreta) {

			item.setObs(duplicado ? "Passagem Duplicada"
					: "Número de Eixos incorreto");
		} else {
			if (passagemPaga) {
				item.setObs("Passagem já cobrada na fatura de "
						+ itemPlanilhaUpload.getDataEmissao());
			} else {
				item.setObs("Verificar Data");
			}

		}
		return item;
	}

	public static void criaExecel(List<List<ItemPlanilhaDownload>> itens,
			String descricao, String caminho) {

		caminho += File.separator + descricao + ".xls";

		// Cria um Arquivo Excel
		Workbook wb = new HSSFWorkbook();

		criaSheetInfo(wb, itens);

		for (int i = 0; i < itens.size(); i++) {

			int row = 0;

			// Cria uma planilha Excel
			Sheet sheet = wb.createSheet("Mês - " + (i + 1));

			// Cria uma linha na Planilha.
			Row cabecalho = sheet.createRow((short) row);

			// Cria as células na linha
			cabecalho.createCell(0).setCellValue("Placa");
			cabecalho.createCell(1).setCellValue("Data");
			cabecalho.createCell(2).setCellValue("Hora");
			cabecalho.createCell(3).setCellValue("Concessionária");
			cabecalho.createCell(4).setCellValue("Praça");
			cabecalho.createCell(5).setCellValue("Valor Cobrado");
			cabecalho.createCell(6).setCellValue("Valor Correto");
			cabecalho.createCell(7).setCellValue("Valor de Restituição");
			cabecalho.createCell(8).setCellValue("Categoria Cobrada");
			cabecalho.createCell(9).setCellValue("Categoria Correta");
			cabecalho.createCell(10).setCellValue("Observações");

			int width = sheet.getColumnWidth(0) * 3;
			sheet.setColumnWidth(0, width);
			sheet.setColumnWidth(1, width);
			sheet.setColumnWidth(2, width);
			sheet.setColumnWidth(3, width);
			sheet.setColumnWidth(4, width);
			sheet.setColumnWidth(5, width);
			sheet.setColumnWidth(6, width);
			sheet.setColumnWidth(7, width);
			sheet.setColumnWidth(9, width);
			sheet.setColumnWidth(8, width);
			sheet.setColumnWidth(9, width);
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

			for (int j = 0; j < itens.get(i).size(); j++) {

				ItemPlanilhaDownload item = itens.get(i).get(j);

				row = row + 1;

				Row dados = sheet.createRow(row);

				dados.createCell(0).setCellValue(item.getPlaca());
				dados.createCell(1).setCellValue(dataFormat(item.getData()));
				dados.createCell(2).setCellValue(horaFormat(item.getHora()));
				dados.createCell(3).setCellValue(item.getConcessionaria());
				dados.createCell(4).setCellValue(item.getPraca());
				dados.createCell(5).setCellValue(doubleFormat(item.getValor()));
				dados.createCell(6).setCellValue(
						doubleFormat(item.getValorCorreto()));
				dados.createCell(7).setCellValue(
						doubleFormat(item.getValorRestituicao()));
				dados.createCell(8).setCellValue(item.getCategoria());
				dados.createCell(9).setCellValue(item.getCategoriaCorreta());
				dados.createCell(10).setCellValue(item.getObs());
			}
			calculaTotal(itens.get(i), (row + 1), sheet);
		}

		try (FileOutputStream fileOut = new FileOutputStream(caminho)) {
			wb.write(fileOut);
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void criaSheetInfo(Workbook wb,
			List<List<ItemPlanilhaDownload>> itens) {

		Double valorCobradoTotal = 0d;
		Double valorCorretoTotal = 0d;
		Double restituicaoTotal = 0d;

		List<Double> valoresCobrados = new ArrayList<Double>();
		List<Double> valoresCorretos = new ArrayList<Double>();
		List<Double> valoresRestituicao = new ArrayList<Double>();

		for (int i = 0; i < itens.size(); i++) {
			Double valorCobrado = 0d;
			Double valorCorreto = 0d;
			Double valorRestituicao = 0d;

			for (int j = 0; j < itens.get(i).size(); j++) {
				ItemPlanilhaDownload item = itens.get(i).get(j);
				valorCobradoTotal += item.getValor();
				valorCobrado += item.getValor();

				valorCorretoTotal += item.getValorCorreto();
				valorCorreto += item.getValorCorreto();

				restituicaoTotal += item.getValorRestituicao();
				valorRestituicao += item.getValorRestituicao();
			}

			valoresCobrados.add(valorCobrado);
			valoresCorretos.add(valorCorreto);
			valoresRestituicao.add(valorRestituicao);
		}

		// Cria uma planilha Excel
		Sheet sheet = wb.createSheet(empresa.getRazaoSocial());

		// Formata a Planilha
		int width = sheet.getColumnWidth(0) * 3;
		sheet.setColumnWidth(0, width);
		sheet.setColumnWidth(1, width);
		sheet.setColumnWidth(2, width);
		sheet.setColumnWidth(3, width);

		CellStyle style = wb.createCellStyle();// Create style
		Font font = wb.createFont();// Create font
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);// Make font bold
		style.setFont(font);// set it to bold

		// Cria uma linha na Planilha.
		Row dados = sheet.createRow((short) 1);

		dados.createCell(0).setCellValue("Dados Resumidos da Correção");
		dados.getCell(0).setCellStyle(style);

		dados = sheet.createRow((short) 2);
		dados.createCell(0).setCellValue("Empresa");
		dados.createCell(1).setCellValue(empresa.getRazaoSocial());
		dados.getCell(0).setCellStyle(style);

		dados = sheet.createRow((short) 3);
		dados.createCell(0).setCellValue("Valor Cobrado Total");
		dados.createCell(1).setCellValue(doubleFormat(valorCobradoTotal));
		dados.getCell(0).setCellStyle(style);

		dados = sheet.createRow((short) 4);
		dados.createCell(0).setCellValue("Valor Correto Total");
		dados.createCell(1).setCellValue(doubleFormat(valorCorretoTotal));
		dados.getCell(0).setCellStyle(style);

		dados = sheet.createRow((short) 5);
		dados.createCell(0).setCellValue("Valor a ser Restituido Total");
		dados.createCell(1).setCellValue(doubleFormat(restituicaoTotal));
		dados.getCell(0).setCellStyle(style);

		dados = sheet.createRow((short) 7);
		dados.createCell(0).setCellValue("Mês");
		dados.createCell(1).setCellValue("Valor Cobrado");
		dados.createCell(2).setCellValue("Valor Correto");
		dados.createCell(3).setCellValue("Valor de Restituição");

		dados.getCell(0).setCellStyle(style);
		dados.getCell(1).setCellStyle(style);
		dados.getCell(2).setCellStyle(style);
		dados.getCell(3).setCellStyle(style);

		int row = 8;
		for (int i = 0; i < valoresCobrados.size(); i++) {
			row += 1;
			dados = sheet.createRow((short) row);
			dados.createCell(0).setCellValue((i + 1));
			dados.createCell(1).setCellValue(
					doubleFormat(valoresCobrados.get(i)));
			dados.createCell(2).setCellValue(
					doubleFormat(valoresCorretos.get(i)));
			dados.createCell(3).setCellValue(
					doubleFormat(valoresRestituicao.get(i)));
		}

	}

	public static void calculaTotal(List<ItemPlanilhaDownload> itens, int row,
			Sheet sheet) {

		Double valorCobrado = 0d;
		Double valorCorreto = 0d;
		Double restituicao = 0d;

		for (int i = 0; i < itens.size(); i++) {
			valorCobrado += itens.get(i).getValor();
			valorCorreto += itens.get(i).getValorCorreto();
			restituicao += itens.get(i).getValorRestituicao();
		}

		Row dados = sheet.createRow(row);

		dados.createCell(4).setCellValue("Totais");
		dados.createCell(5).setCellValue(doubleFormat(valorCobrado));
		dados.createCell(6).setCellValue(doubleFormat(valorCorreto));
		dados.createCell(7).setCellValue(doubleFormat(restituicao));

	}

	public static String dataFormat(Date data) {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		return df.format(data);
	}

	public static String horaFormat(Date hora) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		return df.format(hora);
	}

	public static String doubleFormat(Double valor) {
		return String.format("%.2f", valor);
	}

	
}
