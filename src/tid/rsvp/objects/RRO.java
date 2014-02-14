package tid.rsvp.objects;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import tid.rsvp.RSVPProtocolViolationException;
import tid.rsvp.objects.subobjects.*;


/**

<p>RFC 3209     RSVP-TE		Record Route Object</p>

<p>Routes can be recorded via the RECORD_ROUTE object (RRO).
   Optionally, labels may also be recorded.  The Record Route Class is
   21.  Currently one C_Type is defined, Type 1 Record Route.  The
   RECORD_ROUTE object has the following format:

   Class = 21, C_Type = 1

    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                                                               |
   //                        (Subobjects)                          //
   |                                                               |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

      Subobjects

         The contents of a RECORD_ROUTE object are a series of
         variable-length data items called subobjects.  The subobjects
         are defined in section 4.4.1 below.

   The RRO can be present in both RSVP Path and Resv messages.  If a
   Path message contains multiple RROs, only the first RRO is
   meaningful.  Subsequent RROs SHOULD be ignored and SHOULD NOT be
   propagated.  Similarly, if in a Resv message multiple RROs are
   encountered following a FILTER_SPEC before another FILTER_SPEC is
   encountered, only the first RRO is meaningful.  Subsequent RROs
   SHOULD be ignored and SHOULD NOT be propagated.<p>



 */

public class RRO extends RSVPObject{

	/**
	 * List of Record Route SubObjects contained in an RRO Object.
	 */
	
	private LinkedList<RROSubobject> rroSubobjects;
	
	/**
	 * Log
	 */
		
	private Logger log;
	
	/**
	 * Constructor to be used when a new RRO Object wanted to be attached to a new message
	 */
	
	public RRO(){
		
		classNum = 21;
		cType = 1;
		length = 4;
		rroSubobjects = new LinkedList<RROSubobject>();
		
		log = Logger.getLogger("ROADM");

		log.finest("RRO Object Created");

	}
	
	/**
	 * Constructor to be used when a new RRO Object wanted to be decoded from a received
	 * message.
	 * @param bytes
	 * @param offset
	 */
	
	public RRO(byte[] bytes, int offset){
		
		this.decodeHeader(bytes,offset);
		this.bytes = new byte[this.getLength()];
		rroSubobjects = new LinkedList<RROSubobject>();
		
		log = Logger.getLogger("ROADM");

		log.finest("RRO Object Created");
		
	}
	
	/**
	 * Method to add a new RRO subobject to the ERO object.
	 * @param rroSO
	 */	
	public void addRROubobject(RROSubobject rroSO){
		
		rroSubobjects.add(rroSO);
		
	}
		
	/**
<p>
    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                                                               |
   //                        (Subobjects)                          //
   |                                                               |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
</p>
	 */
	

	public void encode() throws RSVPProtocolViolationException{
		
		// Encontramos la longitud del objeto RRO
		
		this.length = RSVPObjectParameters.RSVP_OBJECT_COMMON_HEADER_SIZE;	// Cabecera 
		
		int subObjectsNumber = rroSubobjects.size();
		for(int i = 0; i < subObjectsNumber; i++){
			
			RROSubobject rroSO = rroSubobjects.get(i);
			this.length = this.length + rroSO.getRrosolength();
			
		}
		
		// Una vez se tiene la longitud completa, se codifica la cabecera
		
		this.bytes = new byte[this.length];
		encodeHeader();
		int currentIndex = RSVPObjectParameters.RSVP_OBJECT_COMMON_HEADER_SIZE;

		// Se codifica cada uno de los subobjetos
		
		for(int i = 0; i < subObjectsNumber; i++){
			
			RROSubobject rroSO = rroSubobjects.get(i);
			rroSO.encode();
			System.arraycopy(rroSO.getSubobject_bytes(), 0, this.bytes, currentIndex, rroSO.getRrosolength());
			currentIndex = currentIndex + rroSO.getRrosolength();
			
		}
		
		
	}
		

	public void decode(byte[] bytes, int offset) throws RSVPProtocolViolationException{

		int unprocessedBytes = this.getLength() - RSVPObjectParameters.RSVP_OBJECT_COMMON_HEADER_SIZE;

		while (unprocessedBytes > 0) {
			int subojectclass=EROSubobject.getType(this.getBytes(), offset);
			int subojectlength=EROSubobject.getLength(this.getBytes(), offset);
			switch(subojectclass) {
				case SubObjectValues.RRO_SUBOBJECT_IPV4ADDRESS:
					IPv4AddressRROSubobject sobjt4=new IPv4AddressRROSubobject(this.getBytes(), offset);
					this.addRROubobject(sobjt4);
					break;
			
				case SubObjectValues.RRO_SUBOBJECT_IPV6ADDRESS:
					IPv6AddressRROSubobject sobjt6=new IPv6AddressRROSubobject(this.getBytes(), offset);
					this.addRROubobject(sobjt6);
					break;		
				
				case SubObjectValues.RRO_SUBOBJECT_LABEL:
					LabelRROSubobject lrroso =new LabelRROSubobject (this.getBytes(), offset);
					addRROubobject(lrroso);
					break;
				default:
					log.warning("RRO Subobject Unknown");
					//FIXME What do we do??
					break;
			}
			offset=offset+subojectlength;
		}
		
	}
	
	// Getters & Setters

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public LinkedList<RROSubobject> getRroSubobjects() {
		return rroSubobjects;
	}

	public void setRroSubobjects(LinkedList<RROSubobject> rroSubobjects) {
		this.rroSubobjects = rroSubobjects;
	}


}
