README.txt

1 - MetaModel convertion.

Management of a proxy in MetaModel storing ontologic equivalent to EMF Objects.
Dependencies and links dynamicaly built using proxy.

EPackage		-> EMFMetaModel
				-> EMFPackageContainer
EClass			-> EMFClassClass
EAttribute		-> EMFAttributeAssociation
				-> EMFAttributeDataProperty (-> EDataType)
				-> EMFAttributeObjectProperty (-> EEnum)
EReference		-> EMFReferenceAssociation
				-> EMFReferenceObjectProperty (-> EClass)
EEnum			-> EMFEnumClass
EEnumElement	-> EMFEnumIndividual
EDataType		-> EMFDataTypeDataType

2 - Model convertion.

Management of a proxy in Model storing ontologic equivalent to EMF Objects.
Dependencies and links dynamicaly built using proxy and MetaModel.

Resource				-> EMFModel
EObject					-> EMFObjectIndividual
EObject + EAttribute	-> EMFObjectIndividualAttributeDataPropertyValue (-> instanceof EDataType)
						-> EMFObjectIndividualAttributeObjectPropertyValue (-> instanceof EEnum)
EObject + EReference	-> EMFObjectIndividualReferenceObjectPropertyValue (-> instanceof EClass)