package org.jcompany.config.domain;

import org.jcompany.config.domain.PlcConfigPattern.Complexity;
import org.jcompany.config.domain.PlcConfigPattern.ExclusionMode;
import org.jcompany.config.domain.PlcConfigPattern.Logic;
import org.jcompany.config.domain.PlcConfigPattern.ModalityLogic;

/**
 * Convenções globais de definição de lógicas MVC-P padroes para Açoes
 *
 */
public class PlcConfigPatternConvention {

	/**
	 * Define a lógica da colaboração
	 */
	private Logic logic;
	/**
	 * Define complexidade da colaboração
	 */
	private Complexity complexity;
	/**
	 * Define como o jCompany vai processar um exclusão
	 * @see ExclusionMode 
	 */
	private ExclusionMode exclusionMode = ExclusionMode.PHYSICAL;
	/**
	 * Define o type de variação da colaboração 
	 */
	private ModalityLogic modality = ModalityLogic.A;
	public Logic logic() {
		return logic;
	}
	public void setLogic(Logic logic) {
		this.logic = logic;
	}
	public Complexity complexity() {
		return complexity;
	}
	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}
	public ExclusionMode exclusionMode() {
		return exclusionMode;
	}
	public void setExclusionMode(ExclusionMode exclusionMode) {
		this.exclusionMode = exclusionMode;
	}
	public ModalityLogic modality() {
		return modality;
	}
	public void setModality(ModalityLogic modality) {
		this.modality = modality;
	}
}
