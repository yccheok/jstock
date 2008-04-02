=====
v0-01
=====
1.  Initial creation.

=====
v0-02
=====
1.  Improve speed of indicators constructing by

    * Avoid saving history to disk, by directly stream the history output to 
    indicator constructing.

    * Only use a single CIMB history server, to avoid extensive server pooling 
    if the history data cannot be found. However, we will be facing inaccurate
    data risk.

2.  User may choose to display a single stock although there is multiple 
    indicators being triggered.

=============
version 0.9.1
=============
1.  Initial release to the public.

=============
version 0.9.2
=============
1.  Fix email feature, which is caused by changes in GMAIL SMTP service.

2.  [ 1776936 ] Load all the entire market stocks into the Real-Time Monitor.


=============
version 0.9.3
=============
1. [ 1830911 ] Bug fixed : Uninstall doesn't remove shortcut.

2. Support color customization.

3. Bug fixed on second sell quantity.

4. Option object as static.


=============
version 0.9.4
=============
1. Added Market Capital and Shares Issued, in Indicator Scanner.

2. Bug fixed. Click on empty area, should clear indicator table selection.

=============
version 0.9.5
=============
1. Bug fixed on stock scanning speed.

2. [1851625] Bug fixed on indicator scanning. We will only download history only when there is a need.

=============
version 0.9.6
=============
1. Bug fixed on getStockBySymbol(String symbol). We forget to break after successl. 
   This cause Indicator Editor Panel takes a long time to retrieve a real time sample data.

2. Bug fixed on IndicatorPanel. Avoid from calling stop when state change. This is to avoid
   GUI from being freezed.

3. Apply soft start/ soft stop in MainFrame, to avoid GUI from being freezed.

4. Bug fixed. Forget to update GUI state.

5. Porfolio feature by using tree table from SwingLabs.