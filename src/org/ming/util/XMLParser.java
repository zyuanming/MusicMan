package org.ming.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParser
{
	private static final MyLogger logger = MyLogger.getLogger("XMLUtils");
	private Element root;

	public XMLParser(InputStream inputstream)
	{
		if (inputstream != null)
		{
			DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory
					.newInstance();
			try
			{
				root = documentbuilderfactory.newDocumentBuilder()
						.parse(inputstream).getDocumentElement();
			} catch (Exception exception)
			{
				logger.e("Create Document Fail: ", exception);
			}
		}
	}

	public XMLParser(byte abyte0[])
	{
		if (abyte0 != null)
		{
			ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(
					abyte0);
			DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory
					.newInstance();
			try
			{
				root = documentbuilderfactory.newDocumentBuilder()
						.parse(bytearrayinputstream).getDocumentElement();
			} catch (Exception exception)
			{
				logger.e("Create Document Fail: ", exception);
			}
		}
	}

	public String getAttributeByTag(String paramString1, String paramString2)
	{
		return null;
		// Element localElement1 = this.root;
		// Object localObject1 = null;
		// if (localElement1 == null)
		// ;
		// NodeList localNodeList;
		// Object localObject2;
		// int i;
		// do
		// {
		// do
		// {
		// return localObject1;
		// localNodeList = this.root.getElementsByTagName(paramString1);
		// localObject2 = null;
		// localObject1 = null;
		// } while (localNodeList == null);
		// i = localNodeList.getLength();
		// localObject1 = null;
		// } while (i == 0);
		// int j = localNodeList.getLength();
		// for (int k = 0;; k++)
		// {
		// if (k >= j)
		// {
		// localObject1 = localObject2;
		// break;
		// }
		// try
		// {
		// Element localElement2 = (Element) localNodeList.item(k);
		// if (localElement2.getNodeType() != 1)
		// continue;
		// Node localNode = localElement2.getAttributes().getNamedItem(
		// paramString2);
		// if (localNode == null)
		// continue;
		// if (localNode == null)
		// {
		// localObject2 = null;
		// } else
		// {
		// String str = localNode.getNodeValue();
		// localObject2 = str;
		// }
		// } catch (Exception localException)
		// {
		// logger.e("getListByTagAndAttribute() error: ", localException);
		// localObject1 = null;
		// }
		// break;
		// }
	}

	public List getListByTag(String s, Class class1)
	{
		ArrayList arraylist = new ArrayList();
		NodeList nodelist;
		if (root == null)
		{
			arraylist = null;
		} else
		{
			nodelist = root.getElementsByTagName(s);
			if (nodelist != null && nodelist.getLength() != 0)
				arraylist = null;
			int i = nodelist.getLength();
			try
			{
				for (int j = 0; j < i; j++)
				{
					Object obj = class1.newInstance();
					Element element = (Element) nodelist.item(j);
					Field afield[];
					afield = class1.getDeclaredFields();
					String s2 = null;
					for (int k = 0; k < afield.length; k++)
					{
						Field field;
						Node node1;
						field = afield[k];
						if (Modifier.isFinal(field.getModifiers()))
						{
							Node node = element.getElementsByTagName(
									field.getName()).item(0);
							if (node != null)
							{
								node1 = node.getFirstChild();
								if (node1 != null)
								{
									String s1 = node1.getNodeValue();
									s2 = s1;
								}
							}
						}
						field.set(obj, s2);
					}
					arraylist.add(obj);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return arraylist;
	}

	public List getListByTagAndAttribute(String paramString, Class paramClass)
	{
		return null;
		// if (this.root == null)
		// ;
		// NodeList localNodeList;
		// for (Object localObject1 = null;; localObject1 = null)
		// {
		// return localObject1;
		// localObject1 = new ArrayList();
		// localNodeList = this.root.getElementsByTagName(paramString);
		// if ((localNodeList != null) && (localNodeList.getLength() != 0))
		// break;
		// }
		// int i = localNodeList.getLength();
		// int j = 0;
		// label63: if (j < i)
		// ;
		// while (true)
		// {
		// Object localObject2;
		// int k;
		// Field localField;
		// Node localNode;
		// try
		// {
		// localObject2 = paramClass.newInstance();
		// Element localElement = (Element) localNodeList.item(j);
		// if (localElement.getNodeType() != 1)
		// break label253;
		// NamedNodeMap localNamedNodeMap = localElement.getAttributes();
		// Field[] arrayOfField = paramClass.getDeclaredFields();
		// k = 0;
		// if (k >= arrayOfField.length)
		// {
		// ((List) localObject1).add(localObject2);
		// break label253;
		// }
		// localField = arrayOfField[k];
		// if (Modifier.isFinal(localField.getModifiers()))
		// break label259;
		// localNode = localNamedNodeMap
		// .getNamedItem(localField.getName());
		// if (localNode != null)
		// break label217;
		// if (localField.getClass() != String.class)
		// break label259;
		// localField.set(localObject2, null);
		// } catch (Exception localException)
		// {
		// logger.e("getListByTagAndAttribute() error: ", localException);
		// localObject1 = null;
		// }
		// break;
		// label217: if (localNode == null)
		// ;
		// String str;
		// for (Object localObject3 = null;; localObject3 = str)
		// {
		// localField.set(localObject2, localObject3);
		// break;
		// str = localNode.getNodeValue();
		// }
		// label253: j++;
		// break label63;
		// break;
		// label259: k++;
		// }
	}

	public List getListByTagAndAttribute(String paramString1,
			String paramString2, Class paramClass)
	{
		return null;
		// if (this.root == null)
		// ;
		// NodeList localNodeList;
		// for (Object localObject1 = null;; localObject1 = null)
		// {
		// return localObject1;
		// localObject1 = new ArrayList();
		// localNodeList = this.root.getElementsByTagName(paramString2);
		// if ((localNodeList != null) && (localNodeList.getLength() != 0))
		// break;
		// }
		// int i = localNodeList.getLength();
		// int j = 0;
		// label67: if (j < i)
		// ;
		// while (true)
		// {
		// Object localObject2;
		// int k;
		// Field localField;
		// Node localNode;
		// try
		// {
		// localObject2 = paramClass.newInstance();
		// Element localElement = (Element) localNodeList.item(j);
		// if (localElement.getNodeType() != 1)
		// break label259;
		// NamedNodeMap localNamedNodeMap = localElement.getAttributes();
		// Field[] arrayOfField = paramClass.getDeclaredFields();
		// k = 0;
		// if (k >= arrayOfField.length)
		// {
		// ((List) localObject1).add(localObject2);
		// break label259;
		// }
		// localField = arrayOfField[k];
		// if (Modifier.isFinal(localField.getModifiers()))
		// break label265;
		// localNode = localNamedNodeMap
		// .getNamedItem(localField.getName());
		// if (localNode != null)
		// break label223;
		// if (localField.getClass() != String.class)
		// break label265;
		// localField.set(localObject2, null);
		// } catch (Exception localException)
		// {
		// logger.e("getListByTagAndAttribute() error: ", localException);
		// localObject1 = null;
		// }
		// break;
		// label223: if (localNode == null)
		// ;
		// String str;
		// for (Object localObject3 = null;; localObject3 = str)
		// {
		// localField.set(localObject2, localObject3);
		// break;
		// str = localNode.getNodeValue();
		// }
		// label259: j++;
		// break label67;
		// break;
		// label265: k++;
		// }
	}

	public List getListByTagsAndAttributeID(String s, String s1, String s2,
			String s3, Class class1)
	{
		return null;
		// Object obj;
		// NodeList nodelist;
		// if(root == null)
		// {
		// obj = null;
		// } else
		// {
		// obj = new ArrayList();
		// if(root.getElementsByTagName(s) == null)
		// {
		// obj = null;
		// } else
		// {
		// label0:
		// {
		// nodelist = root.getElementsByTagName(s);
		// if(nodelist != null && nodelist.getLength() != 0)
		// break label0;
		// obj = null;
		// }
		// }
		// }
		// _L2:
		//
		// _L3:
		// if(j >= i) goto _L2; else goto _L1
		// {
		// return ((List) (obj));
		// int i;
		// int j;
		// i = nodelist.getLength();
		// j = 0;
		// }
		// else
		// {
		// Element element;
		// label1:
		// {
		// element = (Element)nodelist.item(j);
		// if(element.getAttribute(s1).equalsIgnoreCase(s2))
		// break label1;
		// j++;
		// }
		// goto _L3
		// }
		// _L1:
		//
		// NodeList nodelist1;
		// int k;
		// int l;
		// nodelist1 = element.getElementsByTagName(s3);
		// k = nodelist1.getLength();
		// l = 0;
		// _L7:
		// if(l >= k) goto _L2; else goto _L4
		// {
		//
		// }
		// _L4:
		// Object obj1;
		// Element element1;
		// Field afield[];
		// int i1;
		// obj1 = class1.newInstance();
		// element1 = (Element)nodelist1.item(l);
		// afield = class1.getDeclaredFields();
		// i1 = 0;
		// _L8:
		// if(i1 < afield.length) goto _L6; else goto _L5
		// _L5:
		// ((List) (obj)).add(obj1);
		// l++;
		// goto _L7
		// _L6:
		// Field field = afield[i1];
		// if(!Modifier.isFinal(field.getModifiers()))
		// field.set(obj1, element1.getAttribute(field.getName()));
		// break MISSING_BLOCK_LABEL_274;
		// Exception exception;
		// exception;
		// logger.e("getListByTagsAndID() error: ", exception);
		// obj = null;
		// goto _L2
		// i1++;
		// goto _L8
	}

	public List getListByTagsAndID(String paramString1, String paramString2,
			int paramInt, Class paramClass)
	{
		return null;
		// ArrayList localArrayList = new ArrayList();
		// if (this.root == null)
		// localArrayList = null;
		// NodeList localNodeList;
		// while (true)
		// {
		// return localArrayList;
		// if ((this.root.getElementsByTagName(paramString1) == null)
		// || (this.root.getElementsByTagName(paramString1).item(
		// paramInt) == null))
		// {
		// localArrayList = null;
		// } else
		// {
		// localNodeList = ((Element) this.root.getElementsByTagName(
		// paramString1).item(paramInt))
		// .getElementsByTagName(paramString2);
		// if ((localNodeList != null) && (localNodeList.getLength() != 0))
		// break;
		// localArrayList = null;
		// }
		// }
		// int i = localNodeList.getLength();
		// int j = 0;
		// label120: if (j < i)
		// ;
		// while (true)
		// {
		// Object localObject1;
		// int k;
		// Field localField;
		// Node localNode;
		// try
		// {
		// localObject1 = paramClass.newInstance();
		// Element localElement = (Element) localNodeList.item(j);
		// if (localElement.getNodeType() != 1)
		// break label314;
		// NamedNodeMap localNamedNodeMap = localElement.getAttributes();
		// Field[] arrayOfField = paramClass.getDeclaredFields();
		// k = 0;
		// if (k >= arrayOfField.length)
		// {
		// localArrayList.add(localObject1);
		// break label314;
		// }
		// localField = arrayOfField[k];
		// if (Modifier.isFinal(localField.getModifiers()))
		// break label320;
		// localNode = localNamedNodeMap
		// .getNamedItem(localField.getName());
		// if (localNode != null)
		// break label278;
		// if (localField.getClass() != String.class)
		// break label320;
		// localField.set(localObject1, null);
		// } catch (Exception localException)
		// {
		// logger.e("getListByTagsAndID() error: ", localException);
		// localArrayList = null;
		// }
		// break;
		// label278: if (localNode == null)
		// ;
		// String str;
		// for (Object localObject2 = null;; localObject2 = str)
		// {
		// localField.set(localObject1, localObject2);
		// break;
		// str = localNode.getNodeValue();
		// }
		// label314: j++;
		// break label120;
		// break;
		// label320: k++;
		// }
	}

	public List getListForOrderInfo(String s, String s1, Class class1)
	{
		return null;
		// Object obj;
		// NodeList nodelist;
		// if(root == null)
		// {
		// obj = null;
		// } else
		// {
		// obj = new ArrayList();
		// nodelist =
		// ((Element)root.getElementsByTagName(s).item(0)).getElementsByTagName(s1);
		// if(nodelist == null || nodelist.getLength() == 0)
		// obj = null;
		// }
		// _L2:
		// return ((List) (obj));
		// int i;
		// int j;
		// i = nodelist.getLength();
		// j = 0;
		// _L12:
		// if(j < i)
		// {
		// Object obj1;
		// Element element;
		// obj1 = class1.newInstance();
		// element = (Element)nodelist.item(j);
		// if(element.getNodeType() != 1)
		// {
		// j++;
		// goto _L12
		// k++;
		// if(k < afield.length)
		// {
		// Field field;
		// field = afield[k];
		// if(Modifier.isFinal(field.getModifiers()))
		// if(!field.getName().endsWith("type"))
		// {
		// field.set(obj1, element.getFirstChild().getNodeValue());
		// return ((List) (obj));
		// }
		// else
		// {
		// Node node = namednodemap.getNamedItem(field.getName());
		// if(node != null)
		// {
		// s2 = node.getNodeValue();
		// field.set(obj1, s2);
		// logger.e("getListForOrderInfo() error: ", exception);
		// obj = null;
		// return ((List) (obj));
		// }
		// else
		// {
		// String s2 = null;
		// }
		// }
		// }
		// else
		// {
		// ((List) (obj)).add(obj1);
		// }
		// }
		// else
		// {
		// NamedNodeMap namednodemap;
		// Field afield[];
		// int k;
		// namednodemap = element.getAttributes();
		// afield = class1.getDeclaredFields();
		// k = 0;
		// }
		// }
		// else
		// {
		// return ((List) obj);
		// }
	}

	public int getNoteNumberByTagsAndID(String s, String s1, int i, Class class1)
	{
		int j;
		j = -1;
		if (root != null && root.getElementsByTagName(s).item(i) != null)
		{
			NodeList nodelist = ((Element) root.getElementsByTagName(s).item(i))
					.getElementsByTagName(s1);
			if (nodelist == null || nodelist.getLength() == 0)
				j = 0;
			else
				j = nodelist.getLength();
		}
		return j;
	}

	public Element getRoot()
	{
		return this.root;
	}

	public String getValueByTag(String s)
	{
		Element localElement = this.root;
		String s1 = null;
		if (localElement != null)
		{
			NodeList nodelist = root.getElementsByTagName(s);
			if (nodelist != null)
			{
				int i = nodelist.getLength();
				if (i != 0)
				{
					NodeList nodelist1 = nodelist.item(0).getChildNodes();
					int j = 0;
					while (j < nodelist1.getLength())
					{
						if (nodelist1.item(j) != null)
						{
							Node node = nodelist1.item(j);
							if (node != null)
								s1 = (new StringBuilder(String.valueOf(s1)))
										.append(node.getNodeValue()).toString();
						}
						j++;
					}
				}
			}
		}
		return s1;
	}

	public String getValueByTagWithKeyWord(String paramString)
	{
		Element localElement = this.root;
		String str = null;
		if (localElement != null)
		{
			NodeList localNodeList1;
			int i;
			do
			{
				do
				{
					localNodeList1 = this.root
							.getElementsByTagName(paramString);
					str = null;
				} while (localNodeList1 == null);
				i = localNodeList1.getLength();
				str = null;
			} while (i == 0);
			NodeList localNodeList2 = localNodeList1.item(0).getChildNodes();
			int j = 0;
			StringBuffer localStringBuffer = new StringBuffer();
			while (true)
			{
				if (j >= localNodeList2.getLength())
				{
					str = localStringBuffer.toString();
					break;
				}
				Node localNode = localNodeList2.item(j);
				if (localNode != null)
					localStringBuffer.append(localNode.getNodeValue());
				j++;
			}
		}
		return str;
	}
}