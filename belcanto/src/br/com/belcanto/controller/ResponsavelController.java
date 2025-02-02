package br.com.belcanto.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import WebService.CepWebService;
import br.com.belcanto.model.Aluno;
import br.com.belcanto.model.Endereco;
import br.com.belcanto.model.Responsavel;
import br.com.belcanto.services.AlunoServices;
import br.com.belcanto.services.ResponsavelServices;

@ManagedBean
@Controller
@Scope("session")
public class ResponsavelController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private ResponsavelServices responsavelServices;
	@Autowired
	private AlunoServices alunoServices;

	private Endereco endereco;
	private Responsavel responsavel = new Responsavel();
	private List<Responsavel> listaResp;
	private List<Responsavel> listarNomes;
	private List<Aluno> alunos;
	private String nome;
	private String tipoLogradouro;
	private String logradouro;
	private String estado;
	private String cidade;
	private String bairro;
	private String cep = null;
	private boolean exibeBotao;
	private boolean exibeBotaoSalvar;
	private boolean exibirImagemCortada;
	private StreamedContent imagemEnviada;
	private boolean exibirImagem;
	private CroppedImage imagemCortada;
	private String fotoCapturada;
	private String arquivoFotoRecortada;
	private String fotoRecortada;
	private ServletContext servletContext;
	private String total;

	// Metodos de Inicio
	public void init() {

		responsavel = new Responsavel();
		responsavel.setAtivo(true);
		cep = null;
		logradouro = null;
		cidade = null;
		tipoLogradouro = null;
		estado = null;
		bairro = null;
		exibeBotao = false;
		exibeBotaoSalvar = false;
		exibirImagem = false;
		exibirImagemCortada = false;
		imagemEnviada = new StreamedContent() {

			@Override
			public InputStream getStream() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getContentEncoding() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Integer getContentLength() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		imagemCortada = new CroppedImage();

	}
	
	public void initConsulta() {
		nome = null;
		listarNomes = new ArrayList<Responsavel>();
	}
	
	public void initEdit() {
		cep = responsavel.getEndereco().getCep();
		logradouro = responsavel.getEndereco().getLogradouro();
		cidade = responsavel.getEndereco().getCidade();
		tipoLogradouro = responsavel.getEndereco().getTipoLogradouro();
		estado = responsavel.getEndereco().getEstado();
		bairro = responsavel.getEndereco().getBairro();
	}

	// Salvar
	public String salvarResp() {

		responsavel.setEndereco(endereco);

		responsavelServices.salvar(responsavel);
		FacesMessage msm = new FacesMessage("Respons�vel salvo com sucesso!!");
		FacesContext.getCurrentInstance().addMessage(null, msm);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

		return "CadAluno?faces-rediredt=true";
	}

	public String salvarEdit() {
		
		responsavelServices.salvar(responsavel);
		
		FacesMessage msm = new FacesMessage("Respons�vel Editado com Sucesso!!");
		FacesContext.getCurrentInstance().addMessage(null, msm);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

		return "ConResponsavel?faces-rediredt=true";
	}
	// Buscar Cep
	@SuppressWarnings("deprecation")
	public void buscarCep() {
		CepWebService cepWebService = new CepWebService(getCep());
		endereco = new Endereco();
		if (cepWebService.getResultado() == 1) {
			setTipoLogradouro(cepWebService.getTipoLogradouro());
			setLogradouro(cepWebService.getLogradouro());
			setEstado(cepWebService.getEstado());
			setCidade(cepWebService.getCidade());
			setBairro(cepWebService.getBairro());
			endereco.setBairro(bairro);
			endereco.setCep(cep);
			endereco.setCidade(cidade);
			endereco.setEstado(estado);
			endereco.setLogradouro(logradouro);
			endereco.setTipoLogradouro(tipoLogradouro);
			responsavel.setEndereco(endereco);

		} else {
			logradouro = null;
			cidade = null;
			tipoLogradouro = null;
			estado = null;
			bairro = null;
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cep inv�lido ou inexistente!!", "Cep n�o existe"));
			RequestContext.getCurrentInstance().execute("growlColour(\".ui-growl-image-error\");");
		}

	}

	// Carregar Imagem
	@SuppressWarnings("deprecation")
	public void abrirTela() {
		exibeBotao = false;
		exibeBotaoSalvar = false;
		exibirImagem = false;
		exibirImagemCortada = false;
		imagemCortada = null;
		imagemEnviada = new StreamedContent() {

			@Override
			public InputStream getStream() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getContentEncoding() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Integer getContentLength() {
				// TODO Auto-generated method stub
				return null;
			}
		};

		imagemCortada = new CroppedImage();

		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('painelCamera').show();");
	}
	// colocar foto

	public void enviarFoto(FileUploadEvent event) throws IOException {

		byte[] foto = IOUtils.toByteArray(event.getFile().getInputstream());
		responsavel.setFoto(foto);

	}

	public void abrirPasta(FileUploadEvent uploadEvent) throws InterruptedException {
		byte[] img = uploadEvent.getFile().getContents();
		fotoCapturada = uploadEvent.getFile().getFileName();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
		String arquivo = scontext.getRealPath("/resources/imagens/" + fotoCapturada);
		criarArquivo(arquivo, img);
		new Thread().sleep(5000);
		exibeBotao = true;
		exibirImagem = true;

	}

	public StreamedContent getImagemResponsavel() {
		Map<String, String> mapaParametros = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String idRespStr = mapaParametros.get("idResp");
		if (idRespStr != null) {
			Long idResp = new Long(idRespStr);
			Responsavel responsavelBd = responsavelServices.obterPorId(idResp);
			return responsavelBd.getImagem();
		}
		return new DefaultStreamedContent();
	}

	public void criarArquivo(String arquivo, byte[] dados) {

		FileImageOutputStream imageOutput;
		try {
			imageOutput = new FileImageOutputStream(new File(arquivo));
			imageOutput.write(dados, 0, dados.length);
			imageOutput.close();

		} catch (FileNotFoundException ex) {
			Logger.getLogger(ProfessorController.class.getName()).log(Level.SEVERE, null, ex);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Caminho n�o encontrado!" + ex, "Erro"));
		} catch (IOException ex) {
			Logger.getLogger(ProfessorController.class.getName()).log(Level.SEVERE, null, ex);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao criar arquivo!", "Erro"));

		}

	}

	@SuppressWarnings("deprecation")
	public void fecharTela() {
		exibeBotao = false;
		exibeBotaoSalvar = false;
		exibirImagem = false;
		exibirImagemCortada = false;

		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('painelCamera').hide();");

	}

	// Cortar Foto
	private String getNumeroRandomico() {
		int i = (int) (Math.random() * 10000);
		return String.valueOf(i);
	}

	public void botao() {
		exibeBotao = true;
	}

	public void recortar() {

		verificaExistenciaArquivo(arquivoFotoRecortada);
		fotoRecortada = "fotoRecortada" + getNumeroRandomico() + ".png";
		arquivoFotoRecortada = servletContext.getRealPath("") + File.separator + "resources" + File.separator
				+ "imagens" + File.separator + fotoRecortada;
		criarArquivo(arquivoFotoRecortada, imagemCortada.getBytes());
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Foto RECORTADA com sucesso!", "Informa��o"));

	}

	public void cortarImagem() {
		exibirImagemCortada = true;
		exibeBotaoSalvar = true;

		setImagemEnviada(new DefaultStreamedContent(new ByteArrayInputStream(imagemCortada.getBytes())));

		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Foto Recortada com sucesso!", "Informa��o"));
	}

	private void verificaExistenciaArquivo(String arquivo) {
		if (arquivo != null) {
			File file = new File(arquivo);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	// Apagar Foto

	public void apagarFoto() {

		responsavel.setFoto(null);
		FacesMessage msm = new FacesMessage("Foto exclu�da com Sucesso!!");
		FacesContext.getCurrentInstance().addMessage(null, msm);

	}

	// Salvar Imagem Capturada

	@SuppressWarnings("deprecation")
	public void salvarImagem() {

		responsavel.setFoto(imagemCortada.getBytes());
		responsavel.getImagem();

		exibirImagem = false;
		exibirImagemCortada = false;
		FacesMessage msm = new FacesMessage("Imagem Adicionada com sucesso!!");
		FacesContext.getCurrentInstance().addMessage(null, msm);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('painelCamera').hide();");
	}

	public void suscess() {
		FacesMessage msm = new FacesMessage("Imagem Constru�da com sucesso!!");
		FacesContext.getCurrentInstance().addMessage(null, msm);
	}

	// Abrir Responsavel
	public String resp() {
		listaResp = responsavelServices.listar();
		return "CadResponsavel?faces-redirect=true";
	}

	// Abrir CadResponsavel
	@SuppressWarnings("deprecation")
	public void abrirCadResp() {
		listaResp = responsavelServices.listar();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('cadResp').show();");
	}

	
	//Editar
	public String editar(Responsavel responsavel) {
		this.responsavel = responsavel;
		
		return "EditResponsavel?faces-redirect=true";
	}
	
	// Consultas
	public void lista() {
		listaResp = responsavelServices.listar();
	}
	
	public void porNome() {
		listarNomes = responsavelServices.porNome(nome);
		if(listarNomes.isEmpty()) {

			FacesContext.getCurrentInstance().addMessage(null,
			new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum Encontrado!", "Informa��o"));
		}
	}
	
	public void alunos(Responsavel responsavel) {
		alunos = new ArrayList<Aluno>();
		
		alunos = alunoServices.mostrarAlunos(responsavel.getNome());
		if(alunos.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum Aluno Encontrado!", "Informa��o"));
		}else {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('painelAlunos').show();");
		}
		
	}
	// Getters e Setters

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	public List<Responsavel> getListaResp() {
		return listaResp;
	}

	public void setListaResp(List<Responsavel> listaResp) {
		this.listaResp = listaResp;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipoLogradouro() {
		return tipoLogradouro;
	}

	public void setTipoLogradouro(String tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public boolean isExibeBotao() {
		return exibeBotao;
	}

	public void setExibeBotao(boolean exibeBotao) {
		this.exibeBotao = exibeBotao;
	}

	public boolean isExibeBotaoSalvar() {
		return exibeBotaoSalvar;
	}

	public void setExibeBotaoSalvar(boolean exibeBotaoSalvar) {
		this.exibeBotaoSalvar = exibeBotaoSalvar;
	}

	public boolean isExibirImagemCortada() {
		return exibirImagemCortada;
	}

	public void setExibirImagemCortada(boolean exibirImagemCortada) {
		this.exibirImagemCortada = exibirImagemCortada;
	}

	public StreamedContent getImagemEnviada() {
		return imagemEnviada;
	}

	public void setImagemEnviada(StreamedContent imagemEnviada) {
		this.imagemEnviada = imagemEnviada;
	}

	public boolean isExibirImagem() {
		return exibirImagem;
	}

	public void setExibirImagem(boolean exibirImagem) {
		this.exibirImagem = exibirImagem;
	}

	public CroppedImage getImagemCortada() {
		return imagemCortada;
	}

	public void setImagemCortada(CroppedImage imagemCortada) {
		this.imagemCortada = imagemCortada;
	}

	public String getFotoCapturada() {
		return fotoCapturada;
	}

	public void setFotoCapturada(String fotoCapturada) {
		this.fotoCapturada = fotoCapturada;
	}

	public String getArquivoFotoRecortada() {
		return arquivoFotoRecortada;
	}

	public void setArquivoFotoRecortada(String arquivoFotoRecortada) {
		this.arquivoFotoRecortada = arquivoFotoRecortada;
	}

	public String getFotoRecortada() {
		return fotoRecortada;
	}

	public void setFotoRecortada(String fotoRecortada) {
		this.fotoRecortada = fotoRecortada;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public List<Responsavel> getListarNomes() {
		return listarNomes;
	}

	public void setListarNomes(List<Responsavel> listarNomes) {
		this.listarNomes = listarNomes;
	}

	public List<Aluno> getAlunos() {
		return alunos;
	}

	public void setAlunos(List<Aluno> alunos) {
		this.alunos = alunos;
	}
	
}
