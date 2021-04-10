package uk.cmdrnorthpaw.kinventory

class KInventoryConfig {
    /**
     * Whether you want Json pretty printing
     * Recommended if the JSON will ever be read by humans.
     * True by default
     * */
    var prettyPrint = true

    /**
     * Whether empty slots should be serialized in your inventories
     * This will preserve the precise location of ItemStacks within the inventory,
     * but it increases the size of JSON files dramatically
     * because the serializer stores each empty slot as a stack of air.
     * True by default
     * */
    var serializeEmpty = true

}