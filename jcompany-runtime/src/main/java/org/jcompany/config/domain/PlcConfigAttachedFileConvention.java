package org.jcompany.config.domain;

import org.jcompany.commons.PlcFileEntity;

/**
 * Convenções para definição de opçoes de uso de arquivo anexado
 */
public class PlcConfigAttachedFileConvention {

	/**
	 * Define a classe que irá armazenar o arquivo anexado
	 */
	private Class clazz= PlcFileEntity.class;
	
	/**
	 * Define o Tamanho máximo para o arquivo anexado
	 */
	private int maximumSize= 2048;
	
	/**
	* Tamanho ideal, em bytes, para exibição de mensagens de advertência. Se um usuário tenta fazer upload de um arquivo com size maior que o ideal, porém menor que o máximo, o jCompany aceita mas dá uma mensagem de advertência.
	*/
	private int idealSize=1024;
	
	/**
	 * Define a lista de extensões(tipos de arquivo)
	 */
	private String[] extensions= {"gif", "jpg", "png", "txt", "html", "htm" , "jsp", "pdf"};
	
	/**
	 * Se o arquivo é uma Imagem. O default é false.
	 */
	private boolean image= false;

	public Class clazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public int maximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(int maximumSize) {
		this.maximumSize = maximumSize;
	}

	public int idealSize() {
		return idealSize;
	}

	public void setIdealSize(int idealSize) {
		this.idealSize = idealSize;
	}

	public String[] extensions() {
		return extensions;
	}

	public void extensions(String[] extensions) {
		this.extensions = extensions;
	}

	public boolean image() {
		return image;
	}

	public void setImage(boolean image) {
		this.image = image;
	}
}
