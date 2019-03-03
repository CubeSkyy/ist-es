package pt.ulisboa.tecnico.softeng.activity.domain
import javax.transaction.NotSupportedException
import javax.transaction.SystemException
import pt.ist.fenixframework.FenixFramework
import pt.ist.fenixframework.core.WriteOnReadError

abstract class RollbackSpockTestAbstractClass {

	def setup() {
		try {
			FenixFramework.getTransactionManager().begin(false)
			populate4Test()
		} catch(WriteOnReadError|NotSupportedException|SystemException e1) {
			e1.printStackTrace()
		}

	}

	def cleanup() {
		try {
			FenixFramework.getTransactionManager().rollback()
		} catch(IllegalStateException|SecurityException|SystemException e) {
			e.printStackTrace()
		}

	}

	def populate4Test(){
    
  }

}
