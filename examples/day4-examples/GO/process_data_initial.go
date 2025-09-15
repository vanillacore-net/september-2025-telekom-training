func processCSVData(file string) {
    data := readCSVFile(file)
    validated := validateCSVFormat(data)
    transformed := transformCSVData(validated)
    saveToDatabase(transformed)
}

func processJSONData(file string) {
    data := readJSONFile(file)
    validated := validateJSONSchema(data)
    transformed := transformJSONData(validated)
    saveToDatabase(transformed)
}

func processXMLData(file string) {
    data := readXMLFile(file)
    validated := validateXMLSchema(data)
    transformed := transformXMLData(validated)
    saveToDatabase(transformed)
}
