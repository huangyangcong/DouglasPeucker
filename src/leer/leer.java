package leer;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import escribirArchivoGpx.GpxEscritura;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



import java.util.ArrayList;
import java.util.Collections;


public class leer {
	public static ArrayList<Punto> punto=new  ArrayList<Punto>();
	

	public static void main(String[] args) throws IOException, ParserConfigurationException {
		Punto p = new Punto();
		 String ruta = "C:/Transcisa7.gpx";
		  //Y generamos el objeto respecto a la ruta del archivo
		  File archivo = new File(ruta);
		 
		   String info = "";
	        List<String> gpxList = decodeGPX(archivo);
	        for(int i = 0; i < gpxList.size(); i++){
	            info = gpxList.get(i).toString();
	            String[] latlong = info.split(":");
	            	            
	            double latitude = Double.parseDouble(latlong[0]);
	            double longitude = Double.parseDouble(latlong[1]);
	            double ele = Double.parseDouble(latlong[2]);
	            String time = latlong[3];
	            
	            Punto q = new Punto(latitude,longitude,ele,time);
	            punto.add(q);
	            
	            
	        }
	        /*for(int i=0;i<punto.size();i++){
	        	System.out.println("puntosOriginales: "+punto.get(i).toString());
	        }*/
	        
	         List<Punto> distancias=douglasPeucker(punto,0.00025);
	        System.out.println("NUMERO DE PUNTOS ORIGINALES : "+punto.size());
	        
	       
	        
	        for(int i=1;i<distancias.size();i++){
	        	System.out.println("puntosReducidos: "+distancias.get(i).toString());
	        }
	        
	        System.out.println("NUMERO DE PUNTOS REDUCIDOS : "+distancias.size());
	        
	        //se instancia la clase donde contiene las funciones para generar el archivo GPX
	        GpxEscritura gpx =new GpxEscritura();
	        
	        //se llama a la funcion para crear GPx cpn los puntos reducidos
	        GpxEscritura.CrearArchivo(distancias);
	        
	        
	}
	        public static List<Punto> douglasPeucker(List<Punto> puntos,double epsilon){
	        	int maxIndex=0;
	        	double maxDist=0.0;
	        	double distPerp;
	        	List<Punto> izq,der=new ArrayList<Punto>();
	        	
	        	List<Punto> filtro=new ArrayList<Punto>();
	        	
	        	if(puntos.size()<2){
	        		return puntos;
	        	}
	        	
	        	for(int i=1;i<puntos.size()-1;i++){
	        		distPerp=distanciaPerpendicular(puntos.get(i),puntos.get(0),puntos.get(puntos.size()-1));
	        		
	        		if(distPerp>maxDist){
	        			maxIndex=i;
	        			maxDist=distPerp;
	        		}
		        }
	        	
	        	if(maxDist>=epsilon){
	        		izq=douglasPeucker(puntos.subList(0, maxIndex),epsilon);
	        		der=douglasPeucker(puntos.subList(maxIndex,puntos.size()),epsilon);
	        		filtro.addAll(izq);
	        		filtro.addAll(der);
	        	}else{
	        		filtro.add(puntos.get(0));
	        		filtro.add(puntos.get(puntos.size()-1));
	        	}
	        	
	        	
	        	return filtro;
	        	
	        }
	        
	        public static double distanciaPerpendicular(Punto punto,Punto lineaInicio,Punto lineaFin){
	        	
	        	double x=punto.getLatitud();
	        	double y=punto.getLongitud();
	        	
	        	double xi=lineaInicio.getLatitud();
	        	double yi=lineaInicio.getLongitud();
	        	
	        	double xf=lineaFin.getLatitud();
	        	double yf=lineaFin.getLongitud();
	        	
	        	double angulo=(yf-yi)/(xf-xi);
	        	double inter=yi-(angulo*xi);
	        	
	        	double result=Math.abs(angulo*x-y+inter)/Math.sqrt(Math.pow(angulo, 2)+1);
	            
	        	//System.out.println("result : "+result);
	        	return result;
	        	
	        
	        }
	        
	       
	      
	                
	          		        
		        
		 
	
	  private static List<String> decodeGPX(File is){
	        List<String> list = new ArrayList<>();

	        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	        try {
	            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	          //  FileInputStream fileInputStream = new FileInputStream(is);
	          Document document = documentBuilder.parse(is);
	           Element elementRoot = document.getDocumentElement();

	            NodeList nodelist_trkpt = document.getElementsByTagName("trkpt");
	           
	           //NodeList nodelist_ele = document.getElementsByTagName("ele");

	            for(int i = 0; i < nodelist_trkpt.getLength(); i++){
	            	 
	                Node node = nodelist_trkpt.item(i);
	                NamedNodeMap attributes = node.getAttributes();
	                System.out.println("tkrpt: "+i);
	                NodeList children_trkpt=node.getChildNodes();
	                
	                	String ele=children_trkpt.item(1).getTextContent();
	                	String time=children_trkpt.item(3).getTextContent();
	                	System.out.println("hijo: "+i);
	                	System.out.println("ele: "+ele);
	                	System.out.println("time: "+time);
	                
	               //String valorele=ele.getNodeValue();
	                //System.out.println("ele: "+children_trkpt);

	                String newLatitude = attributes.getNamedItem("lat").getTextContent();
	                Double newLatitude_double = Double.parseDouble(newLatitude);

	                String newLongitude = attributes.getNamedItem("lon").getTextContent();
	                Double newLongitude_double = Double.parseDouble(newLongitude);
	                
	                

	                String newLocationName = newLatitude + ":" + newLongitude + ":" + ele + ":" + time;
	                

	                list.add(newLocationName);

	            }

	  //          is.close();

	        } catch (ParserConfigurationException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (SAXException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return list;
	    }
}
