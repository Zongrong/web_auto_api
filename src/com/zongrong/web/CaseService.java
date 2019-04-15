package com.zongrong.web;

public interface CaseService {

	/**
	 * Check whether there is "Test" annotation in the case class.
	 * @param caseClass
	 * @return
	 */
	public boolean isTestAnnotationExist(Class<?> caseClass);
}

