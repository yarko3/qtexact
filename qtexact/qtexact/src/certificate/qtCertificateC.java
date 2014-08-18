/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

package certificate;

import java.util.ArrayList;

import abstractClasses.Certificate;

/**
 * a certificate for quasi threhold obstructions
 * 
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class qtCertificateC<V> extends Certificate<V>
{
	/**
	 * everything is inherited from superclass
	 * @param v
	 * @param f
	 */
	public qtCertificateC(ArrayList<V> v, int f) {
		super(v, f);
	}
	
	
	
}
