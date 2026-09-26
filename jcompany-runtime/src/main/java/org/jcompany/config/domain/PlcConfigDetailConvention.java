package org.jcompany.config.domain;

import java.util.Comparator;

import org.jcompany.commons.comparator.PlcCompareId;

/**
 * @since jCompany 5.0
 * Configurações para definição de classes de detalhe
 */
public class PlcConfigDetailConvention {

	 /**
     * Define a classe detalhe
     */
    private Class clazz;
    
    /**
     * Define o numero padrão de novos detalhes no formulário
     */
    private int numberNew = 4;
    
    /**
     * Define o nome da propriedade no mestre onde está a coleção de detalhes 
     */
    private String collectionName="";
    
    /**
     * Define as regras de cardinalidade para o relacionamento
     */
    private String cardinality= "0..*";
    
    /**
     * propriedade utilizada para desprezar as linhas do detalhe que não deverão ser gravadas,
     * ou seja, se não informada a linha 'registro' não será gravada
     */
    private String referencePropertyDespise= "";
    
    /**
     * Se a coluna flag, com valor "0" (zero) será considerada informada (nulo) ou desconsiderada ("0" como valor)
     */
    private boolean significativeZero= false;
    
    /**
     * Testa unicidade entre os detalhes
     */
    private boolean testDuplicity= true;
    
    /**
     * Define a classe utilizada para a comparação das chaves entre detalhes 
     */
    private Class<? extends Comparator> comparator= PlcCompareId.class;

    /**
     * se informado 'true' o detalhe não será recuperado junto ao grafo do mestre, mas se e somente se,
     * o usuário ir na aba do referido detalhe e pedir para recuperar
     */
    private boolean onDemand= false;
    private boolean onDemandAutomatic= false;

    /**
     * Define as configurações de subdetalhe
     */
    private PlcConfigSubDetailConvention subDetail;

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

	public boolean onDemand() {
		return onDemand;
	}

	public void setOnDemand(boolean onDemand) {
		this.onDemand = onDemand;
	}

	public boolean onDemandAutomatic() {
		return onDemandAutomatic;
	}

	public void setOnDemandAutomatic(boolean onDemandAutomatic) {
		this.onDemandAutomatic = onDemandAutomatic;
	}

	public PlcConfigSubDetailConvention subDetail() {
		return subDetail;
	}

	public void setSubDetail(PlcConfigSubDetailConvention subDetail) {
		this.subDetail = subDetail;
	}
}
