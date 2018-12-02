package main

import (
	"fmt"
	"io/ioutil"
	"strconv"
	"strings"
)

func readFile(filename string) string {
	data, _ := ioutil.ReadFile(filename)
	return string(data)
}

func main() {
	input := strings.Split(readFile("input.txt"), "\n")

	sum := 0

	for _, line := range input {
		number, _ := strconv.Atoi(line)
		sum += number
	}

	fmt.Println(sum)
}
