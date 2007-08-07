package gov.nih.nci.cabig.caaers.rules.objectgraph;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * This class will be used by caAERS Rules Engine.
 * @author vinaykumar
 *
 */

public class FactResolver {
	
	/**
	 *  This method will evalaute a fact that is being asserted. For example a condition which says that - If CTEP is one of the IND holder for this study.
	 *  In that case folllowing argument will be passed - (study,"gov.nih....INDHolder","name","CTEP")
	 * @param sourceObject
	 * @param targetObjectType
	 * @param targetAttributeName
	 * @param targetAttributeValue
	 * @return boolean
	 */
	
  public boolean assertFact(Object sourceObject,
		                    String targetObjectType,
		                    String targetAttributeName,
		                    String targetAttributeValue){
	  
	  
	  
	 
	  
	  NavigationPath np = new NavigationPath();
	  np.setSourceObjectType(sourceObject.getClass().getName());
	  np.setTargetObjectType(targetObjectType);
	  
	  List<Node> pathNodes = np.getNodes();
	  /**
	   * We need minimum two nodes in the path for this method to work.
	   */
	  int size = pathNodes.size();
	  if(size<2){
		  return false;
	  }
	  int i=0;
	  Object sourceObjectInChain = sourceObject;
	  Node sourceNode = null;
	  Node targetNode= null;
	  Iterator<Node> it = pathNodes.iterator();
	  while(it.hasNext()){
		  
		  Object obj=null;
		  if(i<1){
		  sourceNode = it.next();
		  }
		  if(i>0){
			  targetNode = it.next();
			  /**
			   * For M:N relationship
			   */
			  if((sourceNode.isCollection())&&(targetNode.isCollection())){
				  obj = getListOfNextTargetObjectsForEverySourceObject(targetNode,sourceObjectInChain);
			  }
			  /**
			   * For 1:M relationship
			   */
			  if((!sourceNode.isCollection())&&(targetNode.isCollection())){
			  obj = getListOfNextTargetObjects(targetNode,sourceObjectInChain);
			  }
			  /**
			   * For M:1 relationship
			   */
			  if((sourceNode.isCollection())&&(!targetNode.isCollection())){
				 obj = getListOfSingleNextTargetObjectsForEverySourceObject(targetNode,sourceObjectInChain);
			  }
			  /**
			   * For 1:1 relationship
			   */
			  if((!sourceNode.isCollection())&&(!targetNode.isCollection())){
				  obj = getListOfNextTargetObjects(targetNode,sourceObjectInChain);
			  }
			   
			  if(targetNode.isCollection()){
				  if(obj==null){
					  return false;
				  }else{
					  List l = (List)obj;
					  if(l.size()==0){
						  return false;
					  }
				  }
			  }else{
				  if(obj==null){
					  return false;
				  }
			  }
			  
			  sourceNode = targetNode;
			  sourceObjectInChain = obj;
		  }
		  i++;
		  
	  }
	  
	  /**
	   * When you exhausted of all the result then wrap it up
	   */
	 
	  
	  return wrapUp(sourceObjectInChain,targetNode,targetObjectType,
              targetAttributeName,
              targetAttributeValue);
	  
	  
	  
  }
  
  private Object getListOfNextTargetObjectsForEverySourceObject(Node node,Object sourceObject){
	  List list = new ArrayList();
	  String name = node.getName();
	  String methodName = this.getMethodName(name);
	  List outerList = (List)sourceObject;
	  
	  Class class_ = outerList.get(0).getClass();
	  Method method = null;
	  Object obj=null;
	  try {
		    Class[] types = new Class[] {};
		   
		    method = class_.getMethod(methodName, types);
		    
		    Iterator outerIterator = outerList.iterator();
		    while(outerIterator.hasNext()){
		    	
		    	obj = method.invoke(sourceObject, new Object[0]);
		    	
		    	if(obj!=null){
						List innerList = (List)obj;
						Iterator it = innerList.iterator();
						while(it.hasNext()){
							list.add(it.next());
						}
		    	}
		    	
		    }
		
		
		
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchMethodException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	 
	
	  return list;
  }
  
  private Object getListOfNextTargetObjects(Node node,Object nextSourceObject){
	  
	  String name = node.getName();
	  String methodName = this.getMethodName(name);
	  Class class_ = nextSourceObject.getClass();
	  Method method = null;
	  Object obj=null;
	  try {
		  Class[] types = new Class[] {};
		   
			method = class_.getMethod(methodName, types);
			
		obj = method.invoke(nextSourceObject, new Object[0]);
		
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchMethodException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	 
	
	  return obj;
  }
  private Object getListOfSingleNextTargetObjectsForEverySourceObject(Node node,Object nextSourceObject){
		  
	  List listToBeReturned = new ArrayList();
		  String name = node.getName();
		  String methodName = this.getMethodName(name);
		  
		  List list = (List)nextSourceObject;
		  Object conatinedObject = list.get(0);
		  Class class_ = conatinedObject.getClass();
		  Method method = null;
		  Object obj=null;
		  try {
			  Class[] types = new Class[] {}; 
				method = class_.getMethod(methodName, types);
			Iterator it = list.iterator();
			while(it.hasNext()){
				Object objectInList = it.next();
				obj = method.invoke(objectInList, new Object[0]);
				if(obj!=null){
				listToBeReturned.add(obj);
				}
			}
			
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
		
		  return listToBeReturned;
	  }  
  private boolean wrapUp(Object targetObject,
		                 Node targetNode,
		                 String targetObjectType,
          				 String targetAttributeName,
          				 String targetAttributeValue){
	  boolean test = false;
	  Class cls = null;
	  Method method = null;
	  try {
			cls = Class.forName(targetObjectType);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 String methodName = this.getMethodName(targetAttributeName);
	 try {
		 Class[] types = new Class[] {}; 
			method = cls.getMethod(methodName, types);
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchMethodException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  if(targetNode.isCollection()){
		  
		  List list = (List)targetObject;
		  Iterator it = list.iterator();
		 
		  while(it.hasNext()){
			  Object obj = it.next();
			  Object value=null;
			  try {
				value = method.invoke(obj, new Object[0]);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(value.toString().equalsIgnoreCase(targetAttributeValue)){
				test = true;
				break;
			}
		  }
		  
	  }else{
		  
		  Object value=null;
		  try {
				value = method.invoke(targetObject, new Object[0]);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(value.toString().equalsIgnoreCase(targetAttributeValue)){
				test = true;
				
			}
		  
	  }
	  return test;
	  
  }
  private String getMethodName(String name){
	  String prop = "get"+Character.toUpperCase(name.charAt(0)) + name.substring(1);
	  return prop;
  }
  public static void main(String[] args){
	  FactResolver fr = new FactResolver();
	  
  }
}
