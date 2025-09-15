type DataProcessor interface {
    Read(file string) []byte
    Validate(data []byte) bool
    Transform(data []byte) []byte
}

type BaseProcessor struct {
    processor DataProcessor
}

func (b *BaseProcessor) Process(file string) {
    data := b.processor.Read(file)
    if b.processor.Validate(data) {
        transformed := b.processor.Transform(data)
        b.save(transformed)
    }
}

type CSVProcessor struct {
    BaseProcessor
}

func (c *CSVProcessor) Read(file string) []byte {
    // CSV specific reading
}
