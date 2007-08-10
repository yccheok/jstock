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
