public enum LocationType {
    ZILINA, POINT_K, DIVINKA, RAJECKE_TEPLICE, STRECNO;

    public LocationType[] getNextPossibleLocations() {
        switch (this) {
            case ZILINA:
                return new LocationType[]{POINT_K};
            case POINT_K:

                return new LocationType[]{DIVINKA, STRECNO};
            case DIVINKA:
                return new LocationType[]{RAJECKE_TEPLICE};
            case RAJECKE_TEPLICE:
                return new LocationType[]{STRECNO};
            case STRECNO:
                return new LocationType[]{ZILINA};
            default:
                return new LocationType[]{};
        }
    }

}