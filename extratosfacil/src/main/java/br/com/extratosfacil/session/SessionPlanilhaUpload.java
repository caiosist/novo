package br.com.extratosfacil.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.extratosfacil.constantes.Mensagem;
import br.com.extratosfacil.constantes.SAXReader;
import br.com.extratosfacil.constantes.Sessao;
import br.com.extratosfacil.entities.Veiculo;
import br.com.extratosfacil.entities.planilha.ItemPlanilhaDownload;
import br.com.extratosfacil.entities.planilha.ItemPlanilhaUpload;
import br.com.extratosfacil.entities.planilha.PlanilhaUpload;
import br.com.jbc.controller.Controller;

/**
 * Session que representa as regras de negÃ³cios da entidade PlanilhaUpload
 * 
 * @author Paulo Henrique da Silva
 * @since 05/08/2015
 * @version 1.0
 * @category Session
 */

public class SessionPlanilhaUpload {

	/*-------------------------------------------------------------------
	 * 		 					ATTRIBUTES
	 *-------------------------------------------------------------------*/

	private Controller<PlanilhaUpload> controller = new Controller<PlanilhaUpload>();

	public Controller<Veiculo> controllerVeiculo = new Controller<Veiculo>();

	private String dataEmissao = null;

	private String mes = null;

	/*-------------------------------------------------------------------
	 * 		 					GETTERS AND SETTERS
	 *-------------------------------------------------------------------*/

	public Controller<PlanilhaUpload> getController() {
		return controller;
	}

	public Controller<Veiculo> getControllerVeiculo() {
		return controllerVeiculo;
	}

	public void setControllerVeiculo(Controller<Veiculo> controllerVeiculo) {
		this.controllerVeiculo = controllerVeiculo;
	}

	public String getMes() {
		return mes;
	}

	public String getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(String dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public void setController(Controller<PlanilhaUpload> controller) {
		this.controller = controller;
	}

	/*-------------------------------------------------------------------
	 * 		 					METHODS
	 *-------------------------------------------------------------------*/

	/**
	 * MÃ©todo que faz a leitura da planilha de upload e salva
	 * 
	 * @param planilhaUpload
	 * @return
	 */

	public List<ItemPlanilhaDownload> carregaPlanilha(Object workbook) {

		if (workbook instanceof XSSFWorkbook) {
			XSSFSheet sheet = null;
			return this.lerPlanilha(workbook, sheet);
		} else {
			HSSFSheet sheet = null;
			return this.lerPlanilha(workbook, sheet);
		}
	}

	private List<ItemPlanilhaDownload> lerPlanilha(Object workbook, Object sheet) {

		if (!validaMesPlanilha(true)) {
			return null;
		}

		// formatar a data da planilha para o Obj
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Formatar a Hora da Planilha para o objeto
		SimpleDateFormat shf = new SimpleDateFormat("HH:mm:ss");

		// Lista de veiculos da planilha
		List<ItemPlanilhaUpload> lista = new ArrayList<ItemPlanilhaUpload>();

		ItemPlanilhaUpload item = new ItemPlanilhaUpload();

		try {
			Iterator<Row> rowIterator = null;

			if (workbook instanceof XSSFWorkbook) {

				sheet = ((XSSFWorkbook) workbook)
						.getSheet("Passagens de Pedágio");
				// retorna todas as linhas da planilha 0 (aba 1)
				rowIterator = ((XSSFSheet) sheet).iterator();
			} else {
				sheet = ((HSSFWorkbook) workbook)
						.getSheet("Passagens de Pedágio");
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
						item.setCategoria(Integer.valueOf(cell
								.getStringCellValue()));
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

		} finally {
			try {
				if (workbook instanceof XSSFWorkbook) {
					((XSSFWorkbook) workbook).close();
				} else {
					((HSSFWorkbook) workbook).close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}

		return this.makeTheMagic(lista, true);
	}

	public List<ItemPlanilhaDownload> makeTheMagic(
			List<ItemPlanilhaUpload> itensPlanilha, boolean xls) {
		// fazemos a comparacao dos veiculos da planilha upload com os
		// cadastrados

		// Lista de veiculos com cobrancas incorretas
		List<ItemPlanilhaDownload> itensIncorretos = new ArrayList<ItemPlanilhaDownload>();

		// Cria um veiculo temporï¿½rio
		Veiculo temp = new Veiculo();

		// Setar a empresa no veiculo para a busca, no momento esta sendo um
		// objeto vazio, posteriormente pegaremos a empresa da sessao do
		// usuario
		temp.setEmpresa(Sessao.getEmpresaSessao());
		// Buscar todos os veiculos da empresa
		List<Veiculo> veiculos = new ArrayList<Veiculo>();
		HashMap<String, Veiculo> mapaVeiculos = new HashMap<String, Veiculo>();

		try {
			veiculos = this.controllerVeiculo
					.getListByHQLCondition("from Veiculo where empresa_id = "
							+ temp.getEmpresa().getId());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		for (Veiculo veiculo : veiculos) {
			mapaVeiculos.put(veiculo.getPlacaVeiculo(), veiculo);
		}

		Collections.sort(itensPlanilha);

		for (ItemPlanilhaUpload itemPlanilha : itensPlanilha) {
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

			if (isDataIncorreta(itemPlanilha, xls)) {
				itensIncorretos.add(this.criaItemDownload(itemPlanilha, temp,
						false, true));
			}

			if (isDuplicado(itensPlanilha, itemPlanilha)) {
				itensIncorretos.add(this.criaItemDownload(itemPlanilha, temp,
						true, false));
			} else {
				if (itemPlanilha.getCategoria() > temp.getCategoria()) {
					itensIncorretos.add(this.criaItemDownload(itemPlanilha,
							temp, false, false));
				}

			}
		}

		return itensIncorretos;

	}

	private boolean isDataIncorreta(ItemPlanilhaUpload itemPlanilha, boolean xls) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		if (xls) {
			dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		}

		LocalDate dataExtrato = LocalDate.parse(this.dataEmissao, dtf);
		LocalDate dataCobranca = itemPlanilha.getData().toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate();

		Period p = Period.between(dataCobranca, dataExtrato);

		// numero de dias entre as datas
		int x = p.getDays() + (p.getMonths() * 30);

		if (x > 53) {
			return true;
		} else {
			return false;
		}

	}

	private boolean isDuplicado(List<ItemPlanilhaUpload> itensPlanilha,
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

	private ItemPlanilhaDownload criaItemDownload(
			ItemPlanilhaUpload itemPlanilhaUpload, Veiculo temp,
			boolean duplicado, boolean dataIncorreta) {

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
			item.setObs("Verificar Data");
		}
		return item;
	}

	public boolean validaPlanilha(PlanilhaUpload planilhaUpload) {
		planilhaUpload.setEmpresa(Sessao.getEmpresaSessao());
		return true;
	}

	public boolean save(PlanilhaUpload planilhaUpload) {
		if (this.validaPlanilha(planilhaUpload)) {
			try {
				this.controller.insert(planilhaUpload);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	public boolean update(PlanilhaUpload planilhaUpload) {
		if (this.validaPlanilha(planilhaUpload)) {
			try {
				this.controller.update(planilhaUpload);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	public Object validaPlanilha(String path, Object workbook, Object sheet) {

		FileInputStream fisPlanilha = null;

		try {
			File file = new File(path);
			fisPlanilha = new FileInputStream(file);

			if (workbook instanceof XSSFWorkbook) {
				workbook = new XSSFWorkbook(fisPlanilha);
				sheet = ((XSSFWorkbook) workbook)
						.getSheet("Passagens de Pedágio");
			} else {
				workbook = new HSSFWorkbook(fisPlanilha);
				sheet = ((HSSFWorkbook) workbook)
						.getSheet("Passagens de Pedágio");
			}

			if (sheet == null) {
				Mensagem.send(Mensagem.MSG_PLANILHA_ERRADA, Mensagem.ERROR);
				return null;
			}

			if (workbook instanceof XSSFWorkbook) {
				// workbook = new XSSFWorkbook(fisPlanilha);
				sheet = ((XSSFWorkbook) workbook).getSheet("Resumo da Fatura");
			} else {
				// workbook = new HSSFWorkbook(fisPlanilha);
				sheet = ((HSSFWorkbook) workbook).getSheet("Resumo da Fatura");
			}

			if (sheet == null) {
				Mensagem.send(Mensagem.MSG_PLANILHA_ERRADA, Mensagem.ERROR);
				return null;
			} else {
				Iterator<Row> rowIterator = null;
				if (workbook instanceof XSSFWorkbook) {
					rowIterator = ((XSSFSheet) sheet).iterator();
				} else {
					rowIterator = ((HSSFSheet) sheet).iterator();
				}

				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();

					if (row.getRowNum() < 3) {
						continue;
					}

					Iterator<Cell> cellIterator = row.iterator();
					while (cellIterator.hasNext()) {

						Cell cell = cellIterator.next();

						if ((cell.getRowIndex() == 3)
								&& (cell.getColumnIndex() == 1)) {
							if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
								this.mes = cell.getStringCellValue().substring(
										3, 5);
								this.dataEmissao = cell.getStringCellValue();
								break;
							}
						}
					}
				}

			}
		} catch (Exception e) {
			return null;
		} finally {
			try {
				fisPlanilha.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
		return workbook;
	}

	public Object validaPlanilha(String path, boolean xlsx) {
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

	public List<ItemPlanilhaDownload> lerXml(String path) {

		List<ItemPlanilhaUpload> lista = new ArrayList<ItemPlanilhaUpload>();

		SAXReader reader = new SAXReader();
		try {
			lista.addAll(reader.parse(path));
			this.dataEmissao = reader.getDataEmissao();
			this.mes = this.dataEmissao.substring(5, 7);
		} catch (Exception e) {

			e.printStackTrace();
		}

		if (!this.validaMesPlanilha(false)) {
			return null;
		}

		return this.makeTheMagic(lista, false);

	}

	private boolean validaMesPlanilha(boolean xls) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		if (xls) {
			sdf = new SimpleDateFormat("dd/MM/yyyy");
		}

		Date mesCadastro = Sessao.getEmpresaSessao().getDataCadastro();

		Calendar c = Calendar.getInstance();
		c.setTime(mesCadastro);
		c.add(Calendar.MONTH, -2);

		mesCadastro = c.getTime();

		try {
			Date mesPlanilha = sdf.parse(this.dataEmissao);
			if (mesPlanilha.getYear() == mesCadastro.getYear()) {
				if (mesPlanilha.getMonth() < mesCadastro.getMonth()) {
					return false;
				}
			} else {
				mesCadastro.setDate(01);
				mesPlanilha.setDate(01);

				if (mesPlanilha.before(mesCadastro)) {
					return false;
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean validaXml(String caminho) {
		return true;
	}

	public List<ItemPlanilhaDownload> carregaXml(String path) {
		return this.lerXml(path);
	}

	public void clean() {
		// TODO Auto-generated method stub

	}

}