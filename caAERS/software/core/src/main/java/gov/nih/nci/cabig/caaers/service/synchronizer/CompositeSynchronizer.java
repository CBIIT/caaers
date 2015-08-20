/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.synchronizer;

import com.aparzev.lang.StringUtils;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * The intent of this class is to serve as an aggregate for the migration process.
 * Eg:- Incase of {@link gov.nih.nci.cabig.caaers.domain.Study}, the migration consist of migrating basic properties, and individual complex properties.
 *
 * @author Biju Joseph
 *
 * @param <E>
 */
public abstract class CompositeSynchronizer<E extends AbstractMutableDomainObject> implements Synchronizer<E>{
    private static Log logger = LogFactory.getLog(CompositeSynchronizer.class);
	List<Synchronizer<E>> children;
    private boolean stopOnError;
	
	
	public void add(Synchronizer<E> o){
		children.add(o);
	}
	public void remove(Synchronizer<E> o){
		children.remove(o);
	}

	public List<? extends Synchronizer<E>> getChildren() {
		return children;
	}

	public void setChildren(List<Synchronizer<E>> children) {
		this.children = children;
	}

    public boolean getStopOnError() {
        return stopOnError;
    }

    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }

    /**
	 * The realized migrate method is purposely made final. The preMigrate template method is must be specialized.
	 */
	public  void migrate(E src, E dest, DomainObjectImportOutcome<E> outcome) {

        //is the context OK ?
        if(!inAppropriateContext(outcome, this)) return;

		preMigrate(src, dest, outcome);
		if(children != null){
			for(Synchronizer<E> synchronizer : getChildren()){
                if(getStopOnError() && outcome.hasErrors()) {
                    logger.error("Stopping migration due to error");
                    logger.error(outcome.getValidationErrors());
                    return;
                }

                //is the context OK ?
                if(inAppropriateContext(outcome, synchronizer)){
                    synchronizer.migrate(src, dest, outcome);
                }
            }
		}
	}
	
	public abstract void preMigrate(E src, E dest , DomainObjectImportOutcome<E> outcome);

    private boolean inAppropriateContext(DomainObjectImportOutcome<E> outcome, Synchronizer<E> synchronizer) {
        String context = outcome.getContext();
        boolean retVal = true;
        if(StringUtils.isNotEmpty(context)) {
            retVal = synchronizer.contexts().contains(context);
        }

        if(!retVal) {
            outcome.addWarning("SY-CTX-1", "Ignoring synchronization [class : " + synchronizer.getClass().getName() + ", currentContext:  " + String.valueOf(outcome.getContext()) + ", applicableContexts : " + String.valueOf(synchronizer.contexts()) + "]");
        }

        return retVal;
    }
}
