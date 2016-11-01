package de.unipaderborn.visuflow.model.impl;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import de.unipaderborn.visuflow.model.DataModel;
import de.unipaderborn.visuflow.model.VFClass;
import de.unipaderborn.visuflow.model.VFMethod;
import de.unipaderborn.visuflow.model.VFUnit;
import de.unipaderborn.visuflow.model.graph.ICFGStructure;


public class DataModelImpl implements DataModel {
	
	private List<VFClass> classList;
	
	private VFClass selectedClass;
	private VFMethod selectedMethod;
	
	private List<VFMethod> selectedClassMethods;
	private List<VFUnit> selectedMethodUnits;
	
	private EventAdmin eventAdmin;
	
	private ICFGStructure icfg;

    @Override
    public List<VFClass> listClasses() {
    	if(classList == null){
			return Collections.emptyList();
		}
        return classList;
    }

    @Override
    public List<VFMethod> listMethods(VFClass vfClass) {
    	List<VFMethod> methods = Collections.emptyList();
		for (VFClass current : classList) {
			if(current == vfClass) {
				methods = vfClass.getMethods();
			}
		}
		return methods;
    }

    @Override
    public List<VFUnit> listUnits(VFMethod vfMethod) {
    	List<VFUnit> units = Collections.emptyList();
		for (VFClass currentClass : classList) {
			for (VFMethod currentMethod : currentClass.getMethods()) {
				if(currentMethod == vfMethod) {
					units = vfMethod.getUnits();
				}
			}
		}
		return units;
    }

	@Override
	public VFClass getSelectedClass() {
		return selectedClass;
	}

	@Override
	public List<VFMethod> getSelectedClassMethods() {
		if(selectedClassMethods == null){
			return Collections.emptyList();
		}
		return selectedClassMethods;
	}

	@Override
	public List<VFUnit> getSelectedMethodUnits() {
		if(selectedMethodUnits == null){
			return Collections.emptyList();
		}
		return selectedMethodUnits;
	}

	@Override
	public void setSelectedClass(VFClass selectedClass) {
		this.selectedClass = selectedClass;
		this.selectedClassMethods = this.selectedClass.getMethods();
		/*try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		this.setSelectedMethod(this.selectedClass.getMethods().get(1));
	}

	@Override
	public void setSelectedMethod(VFMethod selectedMethod) {
		this.selectedMethod = selectedMethod;
		this.populateUnits();
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put("selectedMethod", selectedMethod);
		properties.put("selectedClassMethods", selectedClassMethods);
		properties.put("selectedMethodUnits", selectedMethodUnits);
		Event modelChanged = new Event(DataModel.EA_TOPIC_DATA_SELECTION, properties);
		eventAdmin.postEvent(modelChanged);
	}

	@Override
	public VFMethod getSelectedMethod() {
		return selectedMethod;
	}

	public void setClassList(List<VFClass> classList) {
		this.classList = classList;
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put("model", classList);
		properties.put("icfg", icfg);
		Event modelChanged = new Event(DataModel.EA_TOPIC_DATA_MODEL_CHANGED, properties);
		eventAdmin.postEvent(modelChanged);
	}
	
	private void populateUnits() {
		this.selectedMethodUnits = this.selectedMethod.getUnits();
	}
	
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	@Override
	public ICFGStructure getIcfg() {
		return icfg;
	}
	
	@Override
	public void setIcfg(ICFGStructure icfg) {
		this.icfg = icfg;
		System.out.println("ICFG " + icfg);
		System.out.println("ICFG size " + icfg.listEdges.size());
	}

}
