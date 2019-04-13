package com.asjngroup.deft.installer.task.propertydefination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyDfnGroup;
import com.asjngroup.deft.common.database.hibernate.util.HibernateUtil;
import com.asjngroup.deft.common.properties.PropertyHelperException;
import com.asjngroup.deft.common.properties.PropertyInstMaintainer;
import com.asjngroup.deft.database.util.StandardDatabaseUpdateComponent;
import com.asjngroup.deft.installer.exception.ConfigurationException;

public class PropertyDfnDatabaseUpdateComponent extends StandardDatabaseUpdateComponent {
	public void updateDatabase() throws ConfigurationException {
		standardProcessObjects();
		standardProcessObjects();

		PropertyInstMaintainer maintainer = new PropertyInstMaintainer(this.sessionFactory);
		try {
			maintainer.maintainPropertyInstGroups();
			maintainer.saveChanges();
		} catch (PropertyHelperException e) {
			throw new ConfigurationException(e);
		}

		try {
			HibernateUtil.delete(this.sessionFactory,
					"from PropertyDfn prd where not exists ( from PropertyDfnGroup pdg where prd.pdgId = pdg.pdgId )");
			HibernateUtil.delete(this.sessionFactory,
					"from PropertyInstGroup pig where not exists ( from PropertyDfnGroup pdg where pig.pdgId = pdg.pdgId )");
			HibernateUtil.delete(this.sessionFactory,
					"from PropertyInst pri where not exists ( from PropertyInstGroup pig where pri.pigId = pig.pigId )");
		} catch (HibernateException e) {
			throw new ConfigurationException(e);
		}
	}

	public void extractFromDatabase() throws ConfigurationException {
	}

	protected Map<Class, List> onPreDeleteVeto(Map<Class, List> deleteObjects) {
		if (deleteObjects.size() == 0) {
			return deleteObjects;
		}

		List<PropertyDfnGroup> pdgObjs = (List) deleteObjects.get(PropertyDfnGroup.class);

		if (pdgObjs == null) {
			return deleteObjects;
		}

		List deletePdgObjs = new ArrayList();
		List keepPdgIds = new ArrayList();

		for (PropertyDfnGroup pdgObj : pdgObjs) {
			if (pdgObj.getDeleteFl())
				deletePdgObjs.add(pdgObj);
			else {
				keepPdgIds.add(Integer.valueOf(pdgObj.getPdgId()));
			}
		}

		List deletePrdObjs = new ArrayList();

		List<PropertyDfn> prdObjs = (List) deleteObjects.get(PropertyDfn.class);
		if (prdObjs != null) {
			for (PropertyDfn prdObj : prdObjs) {
				if (!(keepPdgIds.contains(Integer.valueOf(prdObj.getPdgId())))) {
					deletePrdObjs.add(prdObj);
				}
			}
		}

		deleteObjects.remove(PropertyDfnGroup.class);
		deleteObjects.remove(PropertyDfn.class);

		deleteObjects.put(PropertyDfnGroup.class, deletePdgObjs);
		deleteObjects.put(PropertyDfn.class, deletePrdObjs);

		return deleteObjects;
	}
}