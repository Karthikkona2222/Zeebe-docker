resource "aws_internet_gateway" "camunda_igw" {
  vpc_id = aws_vpc.camunda_vpc.id

  tags = {
    Name = "camunda-igw"
  }
}