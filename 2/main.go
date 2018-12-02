package main

import (
	"fmt"
	"io/ioutil"
	"strings"
)

func readFile(filename string) string {
	data, _ := ioutil.ReadFile(filename)
	return string(data)
}

func frequencies(s string) map[rune]int {
	chars := strings.Split(s, "")
	f := make(map[rune]int)

	for _, char := range chars {
		r := []rune(char)[0]
		f[r]++
	}

	return f
}

func mapValues(m map[rune]int) []int {
	var c []int

	for _, v := range m {
		c = append(c, v)
	}

	return c
}

func checkString(s string) (bool, bool) {
	two, three := false, false
	for _, n := range mapValues(frequencies(s)) {
		if n == 2 {
			two = true
		}

		if n == 3 {
			three = true
		}
	}

	return two, three
}

func stringDifferences(s1, s2 string) int {
	differences := 0

	for i := 0; i < len(s1); i++ {
		if s1[i] != s2[i] {
			differences++
		}
	}

	return differences
}

func main() {
	input := strings.Split(readFile("input.txt"), "\n")

	totalTwo, totalThree := 0, 0

	for _, line := range input {
		two, three := checkString(line)
		if two {
			totalTwo++
		}

		if three {
			totalThree++
		}
	}

	fmt.Println(totalThree * totalTwo)

	for i := 0; i < len(input); i++ {
		for j := 0; j < len(input); j++ {
			if stringDifferences(input[i], input[j]) == 1 {
				fmt.Println(input[i], input[j])
				return
			}
		}
	}
}
