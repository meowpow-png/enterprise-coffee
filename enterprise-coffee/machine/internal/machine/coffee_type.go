package machine

// CoffeeType identifies a coffee beverage.
type CoffeeType string

//goland:noinspection GoUnusedConst
const (
	CoffeeTypeEspresso   CoffeeType = "ESPRESSO"
	CoffeeTypeAmericano  CoffeeType = "AMERICANO"
	CoffeeTypeCappuccino CoffeeType = "CAPPUCCINO"
)
