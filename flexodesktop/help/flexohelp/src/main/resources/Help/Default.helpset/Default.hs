<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE helpset PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN" "../dtd/helpset_2_0.dtd">

<helpset version="1.0">
  <title>Help for FlexoToolSet</title>
  <maps>
    <homeID>FlexoDocResourceCenter</homeID>
    <mapref location="Map.jhm" />
  </maps>
  <view>
    <name>TOC</name>
    <label>Table of content</label>
    <type>javax.help.TOCView</type>
    <data>DefaultTOC.xml</data>
  </view>
  <view>
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>DefaultIndex.xml</data>
  </view>
  <view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">JavaHelpSearch</data>
  </view>
  <presentation default="true" displayviewimages="false">
    <name>primary</name>
    <size width="800" height="600" />
    <location x="100" y="100" />
    <title>Help for FlexoToolSet</title>
    <image>toplevelfolder</image>
    <toolbar>
      <helpaction>javax.help.BackAction</helpaction>
      <helpaction>javax.help.ForwardAction</helpaction>
      <helpaction>javax.help.SeparatorAction</helpaction>
      <helpaction>javax.help.HomeAction</helpaction>
      <helpaction>javax.help.ReloadAction</helpaction>
      <helpaction>javax.help.SeparatorAction</helpaction>
      <helpaction>javax.help.PrintAction</helpaction>
      <helpaction>javax.help.PrintSetupAction</helpaction>
    </toolbar>
  </presentation>
  <presentation default="false" displayviewimages="false">
    <name>main</name>
    <size width="800" height="600" />
    <location x="100" y="100" />
    <title>Help for FlexoToolSet</title>
  </presentation>
</helpset>

