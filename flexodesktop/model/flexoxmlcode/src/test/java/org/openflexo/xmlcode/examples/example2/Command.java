/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.xmlcode.examples.example2;

import java.util.Date;

import org.openflexo.xmlcode.XMLSerializable;


/**
 * Class <code>Command</code> is intented to represent a command object in XML
 * coding/decoding example.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class Command implements XMLSerializable
{

    // Serialized fields
    protected boolean commandIsAlreadyPaid;

    protected ArticlesList articles;

    protected Customer customer;

    protected Date date;

    // Computed fields
    protected int qty;

    protected float commandAmount;

    protected SellReport relatedSellReport;

    public Command()
    {
        super();
        commandAmount = 0;
    }

    @Override
	public String toString()
    {
        String returnedString = "Command (qty=" + qty + ", paid=" + commandIsAlreadyPaid + ", commandAmount=" + commandAmount + " ";
        if (articles != null) {
            returnedString += "," + articles.toString();
        }
        if (customer != null) {
            returnedString += "," + customer.toString();
        }
        if (date != null) {
            returnedString += "," + date.toString();
        }
        returnedString += ")\n";
        return returnedString;
    }

    public void setRelatedSellReport(SellReport aSellReport)
    {
        if (relatedSellReport != null) {
            relatedSellReport.removeFromTotalAmount(commandAmount);
            if (commandIsAlreadyPaid) {
                relatedSellReport.removeFromPaidAmount(commandAmount);
            } else {
                relatedSellReport.removeFromUnpaidAmount(commandAmount);
            }
        }

        relatedSellReport = aSellReport;
        relatedSellReport.addToTotalAmount(commandAmount);
        if (commandIsAlreadyPaid) {
            relatedSellReport.addToPaidAmount(commandAmount);
        } else {
            relatedSellReport.addToUnpaidAmount(commandAmount);
        }
    }

    public int getQty()
    {
        return qty;
    }

    public float getCommandAmount()
    {
        return commandAmount;
    }

    public void setCommandAmount(float aValue)
    {
        if (relatedSellReport != null) {
            relatedSellReport.removeFromTotalAmount(commandAmount);
            if (commandIsAlreadyPaid) {
                relatedSellReport.removeFromPaidAmount(commandAmount);
            } else {
                relatedSellReport.removeFromUnpaidAmount(commandAmount);
            }
        }
        commandAmount = aValue;
        if (relatedSellReport != null) {
            relatedSellReport.addToTotalAmount(commandAmount);
            if (commandIsAlreadyPaid) {
                relatedSellReport.addToPaidAmount(commandAmount);
            } else {
                relatedSellReport.addToUnpaidAmount(commandAmount);
            }
        }
    }

    public boolean getCommandIsAlreadyPaid()
    {
        return commandIsAlreadyPaid;
    }

    public void setCommandIsAlreadyPaid(boolean aBoolean)
    {
        if (commandIsAlreadyPaid != aBoolean) {
            if (commandIsAlreadyPaid) { // command was paid, set to unpaid
                if (relatedSellReport != null) {
                    relatedSellReport.removeFromPaidAmount(commandAmount);
                    relatedSellReport.addToUnpaidAmount(commandAmount);
                }
            } else { // command was unpaid, set to paid
                if (relatedSellReport != null) {
                    relatedSellReport.removeFromUnpaidAmount(commandAmount);
                    relatedSellReport.addToPaidAmount(commandAmount);
                }
            }
        }

        commandIsAlreadyPaid = aBoolean;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer v)
    {
        this.customer = v;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date v)
    {
        this.date = v;
    }

    public ArticlesList getArticles()
    {
        return articles;
    }

    public void setArticles(ArticlesList v)
    {
        System.out.println("setArticles(ArticlesList)");
        this.articles = v;
    }

    public void addToArticles(Movie anArticle)
    {
        System.out.println("addToArticles(Movie)");
        articles.add(anArticle);
        setCommandAmount(commandAmount + anArticle.getPrice());
    }

    public void addToArticles(Object anArticle)
    {
        System.out.println("addToArticles(Object)");
        System.out.println("Sorry, you can only add articles to command");
        System.out.println("Your object is a " + anArticle.getClass().getName());
    }

    public void addToArticles(Album anArticle)
    {
        System.out.println("addToArticles(Album)");
        articles.add(anArticle);
        setCommandAmount(commandAmount + anArticle.getPrice());
    }

    public void addToArticles(MultimediaArticle anArticle)
    {
        System.out.println("addToArticles(MultimediaArticle)");
        articles.add(anArticle);
        setCommandAmount(commandAmount + anArticle.getPrice());
    }

    public void addToArticles(ReggaeAlbum anArticle)
    {
        System.out.println("addToArticles(ReggaeAlbum)");
        articles.add(anArticle);
        setCommandAmount(commandAmount + anArticle.getPrice());
    }

    public void removeFromArticles(MultimediaArticle anArticle)
    {
        System.out.println("removeFromArticles(MultimediaArticle)");
        articles.remove(anArticle);
        setCommandAmount(commandAmount - anArticle.getPrice());
    }

    public void removeFromArticles(Object anArticle)
    {
        System.out.println("removeFromArticles(Object)");
        System.out.println("Sorry, you can only remove articles from command");
    }

}
