/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.nhmwien.schema_mapping_tool.converter;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

/**
 *
 * @author wkoller
 */
public class LocalResolver implements URIResolver {

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
