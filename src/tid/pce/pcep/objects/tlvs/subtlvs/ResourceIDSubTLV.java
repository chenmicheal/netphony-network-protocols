package tid.pce.pcep.objects.tlvs.subtlvs;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import tid.pce.pcep.objects.tlvs.subtlvs.PCEPSubTLVTypes;
import tid.pce.pcep.objects.tlvs.subtlvs.PCEPSubTLV;


/**
 All PCEP TLVs have the following format:

   Type:   2 bytes
   Length: 2 bytes
   Value:  variable

   A PCEP object TLV is comprised of 2 bytes for the type, 2 bytes
   specifying the TLV length, and a value field.

   The Length field defines the length of the value portion in bytes.
   The TLV is padded to 4-bytes alignment; padding is not included in
   the Length field (so a 3-byte value would have a length of 3, but the
   total size of the TLV would be 8 bytes).

   Unrecognized TLVs MUST be ignored.

   IANA management of the PCEP Object TLV type identifier codespace is
   described in Section 9.

In GEYSERS,
ResourceID: unique identifier on 32 bits
         0                   1                   2                   3
         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        |           Type (TBD)          |           Length              |
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        |                          Resource ID                          |
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * 
 * @author Alejandro Tovar de Due�as
 *
 */
public class ResourceIDSubTLV extends PCEPSubTLV {
	
	private Inet4Address ResourceID;
	
	public ResourceIDSubTLV(){
		this.setSubTLVType(PCEPSubTLVTypes.PCEP_SUBTLV_TYPE_RESOURCE_ID);
		
	}
	
	public ResourceIDSubTLV(byte[] bytes, int offset) {
		super(bytes,offset);
		decode();
	}

	/**
	 * Encode RequestedStorageSize TLV
	 */
	public void encode() {
		this.setSubTLVValueLength(4);
		this.subtlv_bytes=new byte[this.getTotalSubTLVLength()];
		this.encodeHeader();
		System.arraycopy(ResourceID.getAddress(), 0, this.subtlv_bytes, 4, 4);
	}

	
	public void decode() {
		byte[] resourceID=new byte[4];
		System.arraycopy(this.subtlv_bytes, 4, resourceID, 0, 4);
		try {
			ResourceID= (Inet4Address)Inet4Address.getByAddress(resourceID);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void setResourceID(Inet4Address ResourceID) {
		this.ResourceID= ResourceID;
	}
	
	public Inet4Address getResourceID() {
		return ResourceID;
	}

}