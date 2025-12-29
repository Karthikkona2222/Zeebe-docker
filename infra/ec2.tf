resource "aws_instance" "camunda_ec2" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  key_name               = var.key_pair_name
  subnet_id              = aws_subnet.public_subnet.id
  vpc_security_group_ids = [aws_security_group.camunda_sg.id]

  associate_public_ip_address = true

  tags = {
    Name = "camunda-ec2"
  }
}
