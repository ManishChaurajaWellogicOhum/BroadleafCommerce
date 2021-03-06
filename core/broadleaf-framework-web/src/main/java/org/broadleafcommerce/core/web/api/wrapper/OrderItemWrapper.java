/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemQualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around OrderItem.
 * For simplicity and most use cases, this wrapper only serializes attributes of <code>DiscreteOrderItem</code>
 * This wrapper should be extended for BundledOrderItems etc...
 *
 * User: Elbert Bautista
 * Date: 4/10/12
 */
@XmlRootElement(name = "orderItem")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OrderItemWrapper extends BaseWrapper implements APIWrapper<OrderItem> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String name;

    @XmlElement
    protected Integer quantity;

    @XmlElement
    protected Money retailPrice;

    @XmlElement
    protected Money salePrice;

    @XmlElement
    protected Long orderId;

    @XmlElement
    protected Long categoryId;

    @XmlElement
    protected Long skuId;

    @XmlElement
    protected Long productId;
    @XmlElement
    protected Boolean isBundle = Boolean.FALSE;

    @XmlElement(name = "orderItemAttribute")
    @XmlElementWrapper(name = "orderItemAttributes")
    protected List<OrderItemAttributeWrapper> orderItemAttributes;
    
    @XmlElement(name = "orderItemPriceDetails")
    @XmlElementWrapper(name = "orderItemPriceDetails")
    protected List<OrderItemPriceDetailWrapper> orderItemPriceDetails;

    //This will only be poulated if this is a BundleOrderItem
    @XmlElement(name = "bundleItem")
    @XmlElementWrapper(name = "bundleItems")
    protected List<OrderItemWrapper> bundleItems;
    //

    @XmlElementWrapper(name = "qualifiers")
    @XmlElement(name = "qualifier")
    protected List<OrderItemQualifierWrapper> qualifiers;

    @XmlElement
    protected Boolean isDiscountingAllowed;

    @Override
    public void wrapDetails(OrderItem model, HttpServletRequest request) {
        this.id = model.getId();
        this.name = model.getName();
        this.quantity = model.getQuantity();
        this.orderId = model.getOrder().getId();
        this.retailPrice = model.getRetailPrice();
        this.salePrice = model.getSalePrice();

        if (model.getCategory() != null) {
            this.categoryId = model.getCategory().getId();
        }

        if (model.getOrderItemAttributes() != null && !model.getOrderItemAttributes().isEmpty()) {
            Map<String, OrderItemAttribute> itemAttributes = model.getOrderItemAttributes();
            this.orderItemAttributes = new ArrayList<OrderItemAttributeWrapper>();
            Set<String> keys = itemAttributes.keySet();
            for (String key : keys) {
                OrderItemAttributeWrapper orderItemAttributeWrapper = 
                        (OrderItemAttributeWrapper) context.getBean(OrderItemAttributeWrapper.class.getName());
                orderItemAttributeWrapper.wrapSummary(itemAttributes.get(key), request);
                this.orderItemAttributes.add(orderItemAttributeWrapper);
            }
        }
        if (model.getOrderItemPriceDetails() != null && !model.getOrderItemPriceDetails().isEmpty()) {
            this.orderItemPriceDetails = new ArrayList<OrderItemPriceDetailWrapper>();
            for (OrderItemPriceDetail orderItemPriceDetail : model.getOrderItemPriceDetails()) {
                OrderItemPriceDetailWrapper orderItemPriceDetailWrapper =
                        (OrderItemPriceDetailWrapper) context.getBean(OrderItemPriceDetailWrapper.class.getName());
                orderItemPriceDetailWrapper.wrapSummary(orderItemPriceDetail, request);
                this.orderItemPriceDetails.add(orderItemPriceDetailWrapper);
            }
        }
        
        if (model instanceof DiscreteOrderItem) {
            DiscreteOrderItem doi = (DiscreteOrderItem) model;
            this.skuId = doi.getSku().getId();
            this.productId = doi.getProduct().getId();
            this.isBundle = false;
            this.isDiscountingAllowed = doi.isDiscountingAllowed();
        } else if (model instanceof BundleOrderItem) {
            BundleOrderItem boi = (BundleOrderItem) model;
            this.skuId = boi.getSku().getId();
            this.productId = boi.getProduct().getId();
            this.isBundle = true;
            this.isDiscountingAllowed = boi.isDiscountingAllowed();
            //Wrap up all the discrete order items for this bundle order item
            List<DiscreteOrderItem> discreteItems = boi.getDiscreteOrderItems();
            if (discreteItems != null && !discreteItems.isEmpty()) {
                this.bundleItems = new ArrayList<OrderItemWrapper>();
                for (DiscreteOrderItem doi : discreteItems) {
                    OrderItemWrapper doiWrapper = (OrderItemWrapper) context.getBean(OrderItemWrapper.class.getName());
                    doiWrapper.wrapSummary(doi, request);
                    this.bundleItems.add(doiWrapper);
                }
            }
            
        }

        if (model.getOrderItemQualifiers() != null && !model.getOrderItemQualifiers().isEmpty()) {
            this.qualifiers = new ArrayList<OrderItemQualifierWrapper>();
            for (OrderItemQualifier qualifier : model.getOrderItemQualifiers()) {
                OrderItemQualifierWrapper qualifierWrapper = (OrderItemQualifierWrapper) context.getBean(OrderItemQualifierWrapper.class.getName());
                qualifierWrapper.wrapSummary(qualifier, request);
                this.qualifiers.add(qualifierWrapper);
            }
        }
    }

    @Override
    public void wrapSummary(OrderItem model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /**
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    
    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    
    /**
     * @return the retailPrice
     */
    public Money getRetailPrice() {
        return retailPrice;
    }

    
    /**
     * @param retailPrice the retailPrice to set
     */
    public void setRetailPrice(Money retailPrice) {
        this.retailPrice = retailPrice;
    }

    
    /**
     * @return the salePrice
     */
    public Money getSalePrice() {
        return salePrice;
    }

    
    /**
     * @param salePrice the salePrice to set
     */
    public void setSalePrice(Money salePrice) {
        this.salePrice = salePrice;
    }

    
    /**
     * @return the orderId
     */
    public Long getOrderId() {
        return orderId;
    }

    
    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    
    /**
     * @return the categoryId
     */
    public Long getCategoryId() {
        return categoryId;
    }

    
    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    
    /**
     * @return the skuId
     */
    public Long getSkuId() {
        return skuId;
    }

    
    /**
     * @param skuId the skuId to set
     */
    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    
    /**
     * @return the productId
     */
    public Long getProductId() {
        return productId;
    }

    
    /**
     * @param productId the productId to set
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    
    /**
     * @return the isBundle
     */
    public Boolean getIsBundle() {
        return isBundle;
    }

    
    /**
     * @param isBundle the isBundle to set
     */
    public void setIsBundle(Boolean isBundle) {
        this.isBundle = isBundle;
    }

    
    /**
     * @return the orderItemAttributes
     */
    public List<OrderItemAttributeWrapper> getOrderItemAttributes() {
        return orderItemAttributes;
    }

    
    /**
     * @param orderItemAttributes the orderItemAttributes to set
     */
    public void setOrderItemAttributes(List<OrderItemAttributeWrapper> orderItemAttributes) {
        this.orderItemAttributes = orderItemAttributes;
    }

    
    /**
     * @return the orderItemPriceDetails
     */
    public List<OrderItemPriceDetailWrapper> getOrderItemPriceDetails() {
        return orderItemPriceDetails;
    }

    
    /**
     * @param orderItemPriceDetails the orderItemPriceDetails to set
     */
    public void setOrderItemPriceDetails(List<OrderItemPriceDetailWrapper> orderItemPriceDetails) {
        this.orderItemPriceDetails = orderItemPriceDetails;
    }

    
    /**
     * @return the bundleItems
     */
    public List<OrderItemWrapper> getBundleItems() {
        return bundleItems;
    }

    
    /**
     * @param bundleItems the bundleItems to set
     */
    public void setBundleItems(List<OrderItemWrapper> bundleItems) {
        this.bundleItems = bundleItems;
    }

    
    /**
     * @return the qualifiers
     */
    public List<OrderItemQualifierWrapper> getQualifiers() {
        return qualifiers;
    }

    
    /**
     * @param qualifiers the qualifiers to set
     */
    public void setQualifiers(List<OrderItemQualifierWrapper> qualifiers) {
        this.qualifiers = qualifiers;
    }

    
    /**
     * @return the isDiscountingAllowed
     */
    public Boolean getIsDiscountingAllowed() {
        return isDiscountingAllowed;
    }

    
    /**
     * @param isDiscountingAllowed the isDiscountingAllowed to set
     */
    public void setIsDiscountingAllowed(Boolean isDiscountingAllowed) {
        this.isDiscountingAllowed = isDiscountingAllowed;
    }

}
