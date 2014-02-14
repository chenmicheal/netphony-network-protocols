package tid.rsvp.objects;

import java.util.logging.Level;
import java.util.logging.Logger;

import tid.rsvp.RSVPProtocolViolationException;

/**

<p>RFC 3209 RSVP-TE		Hello Request Object</p>

<p>5.2. HELLO Object formats

   The HELLO Class is 22.  There are two C_Types defined.

5.2.1. HELLO REQUEST object

   Class = HELLO Class, C_Type = 1

    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                         Src_Instance                          |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                         Dst_Instance                          |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

      Src_Instance: 32 bits

      a 32 bit value that represents the sender's instance.  The
      advertiser maintains a per neighbor representation/value.  This
      value MUST change when the sender is reset, when the node reboots,
      or when communication is lost to the neighboring node and
      otherwise remains the same.  This field MUST NOT be set to zero
      (0).

      Dst_Instance: 32 bits

      The most recently received Src_Instance value received from the
      neighbor.  This field MUST be set to zero (0) when no value has
      ever been seen from the neighbor.


	</p>

 */

public class HelloRequest extends Hello{

	/**
	 *<p>A 32 bit value that represents the sender's instance.  The
      advertiser maintains a per neighbor representation/value.  This
      value MUST change when the sender is reset, when the node reboots,
      or when communication is lost to the neighboring node and
      otherwise remains the same.  This field MUST NOT be set to zero
      (0).</p>
	 */
	
	private int srcInstance;
	
	/**
	 *<p>The most recently received Src_Instance value received from the
      neighbor.  This field MUST be set to zero (0) when no value has
      ever been seen from the neighbor.</p>
	 */
		
	private int dstInstance;
	
	/**
	 * Log
	 */
		
	private Logger log;
	
	/**
	 * Constructor to be used when a new Hello Request Object wanted to be attached 
	 * to a new message.
	 * @param srcInstance
	 * @param dstInstance
	 */
	
	public HelloRequest(int srcInstance, int dstInstance){
		
		classNum = 22;
		cType = 1;
		
		this.srcInstance = srcInstance;
		this.dstInstance = dstInstance;

		length = RSVPObjectParameters.RSVP_OBJECT_COMMON_HEADER_SIZE + 8;

		log = Logger.getLogger("ROADM");

		log.finest("Hello Request Object Created");

	}
	
	/**
	 * Constructor to be used when a new Hello Request Object wanted to be decoded 
	 * from a received message.
	 * @param bytes
	 * @param offset
	 */
	
	public HelloRequest(byte[] bytes, int offset){
		
		this.decodeHeader(bytes,offset);
		this.bytes = new byte[this.getLength()];
		
		log = Logger.getLogger("ROADM");

		log.finest("Hello Request Object Created");
		
	}
	
	/**
	<p>
	0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                         Src_Instance                          |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                         Dst_Instance                          |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

	</p>
	 */
	
	public void encode() throws RSVPProtocolViolationException{
		
		log.finest("Starting Hello Request encode");
		
		encodeHeader();
		int currentIndex = RSVPObjectParameters.RSVP_OBJECT_COMMON_HEADER_SIZE;
		
		bytes[currentIndex] = (byte)((srcInstance>>24) & 0xFF);
		bytes[currentIndex+1] = (byte)((srcInstance>>16) & 0xFF);
		bytes[currentIndex+2] = (byte)((srcInstance>>8) & 0xFF);
		bytes[currentIndex+3] = (byte)(srcInstance & 0xFF);
		
		currentIndex = currentIndex + 4;
		
		bytes[currentIndex] = (byte)((dstInstance>>24) & 0xFF);
		bytes[currentIndex+1] = (byte)((dstInstance>>16) & 0xFF);
		bytes[currentIndex+2] = (byte)((dstInstance>>8) & 0xFF);
		bytes[currentIndex+3] = (byte)(dstInstance & 0xFF);
		
		log.finest("Encoding Hello Request accomplished");
		
		
	}
	
	/**
	<p>
    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                         Src_Instance                          |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                         Dst_Instance                          |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

	</p>
	 */
	
	public void decode(byte[] bytes, int offset) throws RSVPProtocolViolationException{

		log.finest("Starting Hello Request decode");

		int currentIndex = offset + RSVPObjectParameters.RSVP_OBJECT_COMMON_HEADER_SIZE;
		
		srcInstance = (int)(bytes[offset] | bytes[offset+1] | bytes[offset+2] | bytes[offset+3]);
		
		currentIndex = currentIndex + 4;
		
		dstInstance = (int)(bytes[offset] | bytes[offset+1] | bytes[offset+2] | bytes[offset+3]);
		
		log.finest("Decoding Hello Request accomplished");
		
	}
	
	// Getters & Setters

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public int getSrcInstance() {
		return srcInstance;
	}

	public void setSrcInstance(int srcInstance) {
		this.srcInstance = srcInstance;
	}

	public int getDstInstance() {
		return dstInstance;
	}

	public void setDstInstance(int dstInstance) {
		this.dstInstance = dstInstance;
	}
	
	

}
