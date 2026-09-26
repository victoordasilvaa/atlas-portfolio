package org.jcompany.config.domain;

import java.util.Comparator;

import org.jcompany.commons.comparator.PlcCompareId;

/**
 * Configurações para definição de classes de sub-detalhe
 */
public class PlcConfigSubDetailConvention {

	 /**
     * Define a classe de subdetalhe
     */
    private Class clazz = Object.class;
    
    /**
     * Define o numero padrão de novos detalhes no formulário
     */
    private int numberNew= 2;
    
    /**
     * Define o nome da propriedade no mestre onde está a coleção de detalhes 
     */
    private String collectionName="";
    
    /**
     * Define as regras de cardinalidade para o relacionamento
     */
    private String cardinality= "0..*";
    
    /**
     * propriedade utilizada para desprezar as linhas do subDetalhe que não deverão ser gravadas, ou seja,
     * se não informada a linha 'registro' não será gravada
     */
    private String referencePropertyDespise= "";
    
    /**
     * Se a coluna flag, com valor "0" (zero) será considerada informada (nulo) ou desconsiderada ("0" como valor)
     */
    private boolean significativeZero=false;
    
    /**
     * Testa unicidade entre os detalhes
     */
    private boolean testDuplicity=true;
    
    /**
     * Define a classe utilizada para a comparação das chaves entre detalhes 
     */
    private Class<? extends Comparator> comparator=PlcCompareId.class;

	public Class clazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public int numberNew() {
		return numberNew;
	}

	public void setNumberNew(int numberNew) {
		this.numberNew = numberNew;
	}

	public String collectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String cardinality() {
		return cardinality;
	}

	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}

	public String referencePropertyDespise() {
		return referencePropertyDespise;
	}

	public void setReferencePropertyDespise(String referencePropertyDespise) {
		this.referencePropertyDespise = referencePropertyDespise;
	}

	public boolean significativeZero() {
		return significativeZero;
	}

	public void setSignificativeZero(boolean significativeZero) {
		this.significativeZero = significativeZero;
	}

	public boolean testDuplicity() {
		return testDuplicity;
	}

	public void setTestDuplicity(boolean testDuplicity) {
		this.testDuplicity = testDuplicity;
	}

	public Class<? extends Comparator> comparator() {
		return comparator;
	}

	public void setComparator(Class<? extends Comparator> comparator) {
		this.comparator = comparator;
	}

}
