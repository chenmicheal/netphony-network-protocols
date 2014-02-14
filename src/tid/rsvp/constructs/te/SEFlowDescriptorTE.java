package tid.rsvp.constructs.te;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import tid.rsvp.RSVPProtocolViolationException;
import tid.rsvp.constructs.SEFlowDescriptor;
import tid.rsvp.objects.FlowSpec;
import tid.rsvp.objects.RSVPObject;
import tid.rsvp.objects.RSVPObjectParameters;


/**
 * 
      <SE flow descriptor> ::= <FLOWSPEC> <SE filter spec list>

      <SE filter spec list> ::= <SE filter spec>
                               | <SE filter spec list> <SE filter spec>

      <SE filter spec> ::=     <FILTER_SPEC> <LABEL> [ <RECORD_ROUTE> ]

      Note:  LABEL and RECORD_ROUTE (if present), are bound to the
             preceding FILTER_SPEC.  No more than one LABEL and/or
             RECORD_ROUTE may follow each FILTER_SPEC.

  

 * @author Fernando Mu�oz del Nuevo fmn@tid.es
 *
 */

public class SEFlowDescriptorTE extends SEFlowDescriptor {

	/**
	 * Flow Spec is a mandatory object when SE style is used and it is its first appearance
	 * in the flow descriptor list
	 */
	
	private FlowSpec flowSpec;
	
	/**
	 * Filter Spec is an optional object collection when SE style is used.
	 * It at least has to contain one Filter Spec Object
	 */
	
	private LinkedList<FilterSpecTE> filterSpecTEList;
	
	
	/**
	 * Log
	 */
	
	private Logger log;
	
	/**
	 * Constructor to be used when a SE Flow Descriptor TE is received and it is wanted to decode it
	 */
	
	public SEFlowDescriptorTE() {
		
		log = Logger.getLogger("ROADM");
		log.setLevel(Level.ALL);
		filterSpecTEList = new LinkedList<FilterSpecTE>();
		log.finest("FF Flow Descriptor TE Created");
		
	}
	
	/**
	 * Constructor to be used when a new SE Flow Descriptor TE it wanted to be created and sent 
	 * @param flowSpec
	 * @param filterSpec
	 * @throws RSVPProtocolViolationException It is thrown when a mandatory field is not present
	 */
		
	public SEFlowDescriptorTE(FlowSpec flowSpec, FilterSpecTE filterSpec) throws RSVPProtocolViolationException{
		
		log = Logger.getLogger("ROADM");
		log.setLevel(Level.ALL);
		
		if(flowSpec != null){
		
			this.length = this.length + flowSpec.getLength();
			this.flowSpec = flowSpec;
			log.finest("Flow Spec found");
			
		}else{	
			
			// Campo obligatorio, por lo tanto se lanza excepcion si no existe
			log.severe("Flow Spec not found, It is mandatory");
			throw new RSVPProtocolViolationException();
			
		}
		if(filterSpec != null){
			
			this.length = this.length + filterSpec.getLength();
			filterSpecTEList = new LinkedList<FilterSpecTE>();
			filterSpecTEList.add(filterSpec);
			log.finest("Filter Spec TE found");
			
		}else{	
			
			// Campo obligatorio, por lo tanto se lanza excepcion si no existe
			log.severe("Filter Spec TE not found, It is mandatory");
			throw new RSVPProtocolViolationException();
			
		}

		log.finest("SE Flow Descriptor TE Created");
		
	}
	
	/**
	 * Method to add new Filter Spec TE object to the filter Spec list
	 * @param filterSpec
	 */
	
	public void addFilterSpec(FilterSpecTE filterSpec){
		
		filterSpecTEList.add(filterSpec);
		this.length = this.length + filterSpec.getLength();
		
	}
		
	/**
	 * 
	 * SE Flow Descriptor TE encoding method. In failure case it throws an exception.
	 * 
	 * @throws RSVPProtocolViolationException 
	 * 
	 */
			
	public void encode() throws RSVPProtocolViolationException{
		
		log.finest("Starting SE Flow Descriptor TE Encode");
		
		this.bytes = new byte[length];
		
		int offset=0;
		if(flowSpec != null){
			// Campo obligatorio
			flowSpec.encode();
			System.arraycopy(flowSpec.getBytes(), 0, bytes, offset, flowSpec.getLength());
			offset = offset + flowSpec.getLength();
		}else{
			
			log.severe("Mandatory field Flow Spec not found");
			throw new RSVPProtocolViolationException();
			
		}
		
		int fslSize = filterSpecTEList.size();
		
		if(fslSize >0){
			for(int i = 0; i < fslSize; i++){
				
								
				// Recorro todos los flow descriptor y los codifico
				
				FilterSpecTE fs = filterSpecTEList.get(i);
				fs.encode();
				System.arraycopy(fs.getBytes(), 0, bytes, offset, fs.getLength());
				offset = offset + fs.getLength();
				
			}
		}else{
			
			log.severe("Mandatory field Filter Spec TE not found");
			throw new RSVPProtocolViolationException();
		}
		log.finest("Encoding SE Flow Descriptor TE Accomplished");
		
	}

	/**
	 * 
	 * SE Flow Descriptor decoding method. In failure case it throws an exception.
	 * 
	 * @throws RSVPProtocolViolationException 
	 */
	
	public void decode(byte[] bytes, int offset) throws RSVPProtocolViolationException {
		
		log.finest("SE Flow Descriptor Decode");
		
		int classNum = RSVPObject.getClassNum(bytes,offset);
		int cType = RSVPObject.getcType(bytes, offset);
		int length = 0;
		int bytesLeft = bytes.length - offset; 
		
		if(classNum == RSVPObjectParameters.RSVP_OBJECT_CLASS_FLOW_SPEC){
			
			if(cType == 2){		// Tipo Correcto
				
				flowSpec = new FlowSpec(bytes,offset);
				
			}else{
				
				// No se ha formado correctamente el objeto sender template
				log.severe("Malformed Flow Spec cType field");
				throw new RSVPProtocolViolationException();
				
			}
			flowSpec.decode(bytes,offset);
			offset = offset + flowSpec.getLength();
			length = length + flowSpec.getLength();
			bytesLeft = bytesLeft - flowSpec.getLength();
			log.finest("Sender Template decoded");
			
		}else{
			
			// No se ha formado correctamente el objeto sender template
			log.severe("Malformed SE Flow Descriptor, Flow Spec object not found");
			throw new RSVPProtocolViolationException();
			
		}
		classNum = RSVPObject.getClassNum(bytes,offset);
		cType = RSVPObject.getcType(bytes,offset);
		if(classNum == RSVPObjectParameters.RSVP_OBJECT_CLASS_FILTER_SPEC){
			
			if(cType == 7){		// FilterSpecLSPTunnelIPv4
				
				FilterSpecTE filterSpecTE = new FilterSpecTE();
				filterSpecTE.decode(bytes,offset);
				filterSpecTEList.add(filterSpecTE);
				offset = offset + filterSpecTE.getLength();
				length = length + filterSpecTE.getLength();
				bytesLeft = bytesLeft - filterSpecTE.getLength();
				
			}else if(cType == 8){		// FilterSpecLSPTunnelIPv6
				
				FilterSpecTE filterSpecTE = new FilterSpecTE();
				filterSpecTE.decode(bytes,offset);
				filterSpecTEList.add(filterSpecTE);
				offset = offset + filterSpecTE.getLength();
				length = length + filterSpecTE.getLength();
				bytesLeft = bytesLeft - filterSpecTE.getLength();
			
			}else{
				
				// No se ha formado correctamente el objeto Filter Spec
				log.severe("Malformed Filter Spec cType field");
				throw new RSVPProtocolViolationException();
				
			}
			log.finest("Filter Spec decoded");
		}
		while(bytesLeft > 0){
			
			// Comprobamos la existencia de mas filter specs
			
			classNum = RSVPObject.getClassNum(bytes,offset);
			cType = RSVPObject.getcType(bytes,offset);
			if(classNum == RSVPObjectParameters.RSVP_OBJECT_CLASS_FILTER_SPEC){
				
				if(cType == 7){		// FilterSpecLSPTunnelIPv4
					
					FilterSpecTE filterSpecTE = new FilterSpecTE();
					filterSpecTE.decode(bytes,offset);
					filterSpecTEList.add(filterSpecTE);
					offset = offset + filterSpecTE.getLength();
					length = length + filterSpecTE.getLength();
					bytesLeft = bytesLeft - filterSpecTE.getLength();
					
				}else if(cType == 8){		// FilterSpecLSPTunnelIPv6
					
					FilterSpecTE filterSpecTE = new FilterSpecTE();
					filterSpecTE.decode(bytes,offset);
					filterSpecTEList.add(filterSpecTE);
					offset = offset + filterSpecTE.getLength();
					length = length + filterSpecTE.getLength();
					bytesLeft = bytesLeft - filterSpecTE.getLength();
				
				}else{
					
					// No se ha formado correctamente el objeto Filter Spec
					log.severe("Malformed Filter Spec cType field");
					throw new RSVPProtocolViolationException();
					
				}
				log.finest("Filter Spec decoded");
					
			}else{
				// Otro objeto diferente
				log.severe("Filter Spec expected and not found");
				throw new RSVPProtocolViolationException();			
			}
		
		}
			
		this.setLength(length);
		log.finest("Decoding FF Flow Descriptor TE Accomplished");
		

	}

	// Getters and Setters
	
	public FlowSpec getFlowSpec() {
		return flowSpec;
	}

	public void setFlowSpec(FlowSpec flowSpec) {
		this.flowSpec = flowSpec;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public LinkedList<FilterSpecTE> getFilterSpecTEList() {
		return filterSpecTEList;
	}

	public void setFilterSpecTEList(LinkedList<FilterSpecTE> filterSpecTEList) {
		this.filterSpecTEList = filterSpecTEList;
	}

	
}
