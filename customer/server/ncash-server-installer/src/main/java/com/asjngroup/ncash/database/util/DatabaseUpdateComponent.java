package com.asjngroup.ncash.database.util;

import org.dom4j.Document;
import org.hibernate.SessionFactory;

import javax.xml.datatype.DatatypeConfigurationException;

import com.asjngroup.ncash.common.database.datasource.DataSource;
import com.asjngroup.ncash.common.database.schema.Schema;

public interface DatabaseUpdateComponent
{
	public void initialise( SessionFactory sessionFactory, DataSource dataSource, Schema schema, Document doc ) throws DatatypeConfigurationException;

	public void updateDatabase() throws DatatypeConfigurationException;

	public void extractFromDatabase() throws DatatypeConfigurationException;
}
